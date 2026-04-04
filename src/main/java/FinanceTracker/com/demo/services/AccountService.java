package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.AccountDto;
import FinanceTracker.com.demo.entities.Account;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.repositories.AccountRepository;
import FinanceTracker.com.demo.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserService userService, ModelMapper modelMapper, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.transactionRepository = transactionRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }


    public Account createAccount(AccountDto accountDto) {
        User currentUser = getCurrentUser();
        Account account = modelMapper.map(accountDto, Account.class);
        account.setUser(currentUser);

        return accountRepository.save(account);
    }

    public List<Account> getAllUserAccounts() {
        User currentUser = getCurrentUser();
        return accountRepository.findAllByUserId(currentUser.getId());
    }

    public Account getAccountById(Long id) {
        User currentUser = getCurrentUser();
        return accountRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Account not found or access denied."));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, AccountDto accountDto) {
        User currentUser = getCurrentUser();
        Account existingAccount = accountRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Account not found or access denied."));

        existingAccount.setName(accountDto.getName());
        existingAccount.setType(accountDto.getType());

        existingAccount.setCurrentBalance(accountDto.getCurrentBalance());


        return accountRepository.save(existingAccount);
    }

    public void deleteAccount(Long id) {
        User currentUser = getCurrentUser();
        Account existingAccount = accountRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Account not found or access denied."));

        if (!transactionRepository.findByAccountId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete account: Account has linked transactions.");
        }

        accountRepository.delete(existingAccount);
    }
}