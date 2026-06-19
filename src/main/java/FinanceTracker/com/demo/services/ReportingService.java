package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.BudgetSummaryDto;
import FinanceTracker.com.demo.dto.FinancialReportDto;
import FinanceTracker.com.demo.entities.Budget;
import FinanceTracker.com.demo.entities.Category;
import FinanceTracker.com.demo.entities.Transaction;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.repositories.AccountRepository;
import FinanceTracker.com.demo.repositories.BudgetRepository;
import FinanceTracker.com.demo.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public ReportingService(UserService userService, TransactionRepository transactionRepository,
                            BudgetRepository budgetRepository, AccountRepository accountRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    public FinancialReportDto generateFinancialReport(LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentUser();
        Long userId = currentUser.getId();

        List<Transaction> allTransactions = transactionRepository.findAllByUserId(userId);

        List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(t -> !t.getTransactionDate().toLocalDate().isBefore(startDate) &&
                        !t.getTransactionDate().toLocalDate().isAfter(endDate))
                .toList();

        BigDecimal totalIncome = filteredTransactions.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = filteredTransactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        BigDecimal totalCurrentBalance = accountRepository.findAllByUserId(userId).stream()
                .map(account -> account.getCurrentBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BudgetSummaryDto> budgetSummaries = processBudgets(userId, filteredTransactions);

        return FinancialReportDto.builder()
                .reportDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .totalCurrentBalance(totalCurrentBalance)
                .budgetSummaries(budgetSummaries)
                .build();
    }

    private List<BudgetSummaryDto> processBudgets(Long userId, List<Transaction> transactions) {

        List<Budget> userBudgets = budgetRepository.findAllByUserId(userId);

        // ✅ group by Category object
        Map<Category, List<Transaction>> groupedExpenses =
                transactions.stream()
                        .filter(t -> "EXPENSE".equalsIgnoreCase(t.getTransactionType()))
                        .filter(t -> t.getCategory() != null)
                        .collect(Collectors.groupingBy(Transaction::getCategory));

        return userBudgets.stream()
                .map(budget -> {
                    Category category = budget.getCategory();
                    String categoryName = category != null ? category.getName() : "Uncategorized";
                    BigDecimal budgetAmount = budget.getMaxAmount();

                    BigDecimal actualExpenses = groupedExpenses
                            .getOrDefault(category, List.of())
                            .stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // ✅ Fixed: حساب remaining و percentageUsed و isOverBudget
                    BigDecimal remaining = budgetAmount.subtract(actualExpenses);
                    boolean isOverBudget = actualExpenses.compareTo(budgetAmount) > 0;
                    double percentageUsed = budgetAmount.compareTo(BigDecimal.ZERO) > 0
                            ? actualExpenses.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
                            : 0.0;

                    return BudgetSummaryDto.builder()
                            .budgetId(budget.getId())
                            .budgetName(budget.getName())
                            .categoryName(categoryName)
                            .budgetAmount(budgetAmount)
                            .actualSpend(actualExpenses)
                            .remaining(remaining)
                            .percentageUsed(percentageUsed)
                            .isOverBudget(isOverBudget)
                            .startDate(budget.getStartDate())
                            .endDate(budget.getEndDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}