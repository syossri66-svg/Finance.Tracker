package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.TransactionDto;
import FinanceTracker.com.demo.entities.Account;
import FinanceTracker.com.demo.entities.Category;
import FinanceTracker.com.demo.entities.Transaction;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserService userService,
                              AccountService accountService, CategoryService categoryService, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    private void updateAccountBalance(Account account, BigDecimal amount, boolean isIncome, boolean isReversal) {
        BigDecimal currentBalance = account.getCurrentBalance();
        BigDecimal newBalance;

        if (isIncome) {
            newBalance = isReversal ? currentBalance.subtract(amount) : currentBalance.add(amount);
        } else {
            newBalance = isReversal ? currentBalance.add(amount) : currentBalance.subtract(amount);
        }

        account.setCurrentBalance(newBalance);
        accountService.save(account);
    }


    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) {
        User currentUser = getCurrentUser();

        Account account = accountService.getAccountById(transactionDto.getAccountId());
        Category category = categoryService.getCategoryEntityById(transactionDto.getCategoryId());
        Transaction transaction = modelMapper.map(transactionDto, Transaction.class);
        transaction.setUser(currentUser);
        transaction.setAccount(account);
        transaction.setCategory(category);

        boolean isIncome = "INCOME".equalsIgnoreCase(transactionDto.getTransactionType());
        updateAccountBalance(account, transactionDto.getAmount(), isIncome, false);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionDto transactionDto) {
        User currentUser = getCurrentUser();

        Transaction existingTransaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found or access denied."));

        Account oldAccount = existingTransaction.getAccount();
        BigDecimal oldAmount = existingTransaction.getAmount();
        boolean oldIsIncome = "INCOME".equalsIgnoreCase(existingTransaction.getTransactionType());

        updateAccountBalance(oldAccount, oldAmount, oldIsIncome, true);

        Account newAccount = accountService.getAccountById(transactionDto.getAccountId());
        Category newCategory = categoryService.getCategoryEntityById(transactionDto.getCategoryId());

        existingTransaction.setAccount(newAccount);
        existingTransaction.setCategory(newCategory);
        existingTransaction.setAmount(transactionDto.getAmount());
        existingTransaction.setTransactionType(transactionDto.getTransactionType());
        existingTransaction.setDescription(transactionDto.getDescription());
        existingTransaction.setTransactionDate(transactionDto.getTransactionDate());

        BigDecimal newAmount = transactionDto.getAmount();
        boolean newIsIncome = "INCOME".equalsIgnoreCase(transactionDto.getTransactionType());

        updateAccountBalance(newAccount, newAmount, newIsIncome, false);

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        User currentUser = getCurrentUser();
        Transaction existingTransaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found or access denied."));

        Account account = existingTransaction.getAccount();
        BigDecimal amount = existingTransaction.getAmount();
        boolean isIncome = "INCOME".equalsIgnoreCase(existingTransaction.getTransactionType());

        updateAccountBalance(account, amount, isIncome, true);

        transactionRepository.delete(existingTransaction);
    }

    public List<Transaction> getAllUserTransactions() {
        User currentUser = getCurrentUser();
        return transactionRepository.findAllByUserId(currentUser.getId());
    }

    public Transaction getTransactionById(Long id) {
        User currentUser = getCurrentUser();
        return transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found or access denied."));
    }
}