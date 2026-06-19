package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.BudgetDto;
import FinanceTracker.com.demo.entities.Budget;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserService userService;

    @Autowired
    public BudgetServiceImpl(BudgetRepository budgetRepository, UserService userService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    @Override
    public Budget createBudget(BudgetDto budgetDto) {
        User currentUser = getCurrentUser();
        Budget budget = new Budget();
        budget.setName(budgetDto.getName());
        budget.setStartDate(budgetDto.getStartDate());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setMaxAmount(budgetDto.getMaxAmount());
        budget.setUser(currentUser);
        return budgetRepository.save(budget);
    }

    @Override
    public List<Budget> getAllUserBudgets() {
        User currentUser = getCurrentUser();
        return budgetRepository.findAllByUserId(currentUser.getId());
    }

    @Override
    public Budget getBudgetById(Long id) {
        User currentUser = getCurrentUser();
        return budgetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Budget not found or access denied."));
    }

    @Override
    public Budget updateBudget(Long id, BudgetDto budgetDto) {
        Budget budget = getBudgetById(id);
        budget.setName(budgetDto.getName());
        budget.setStartDate(budgetDto.getStartDate());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setMaxAmount(budgetDto.getMaxAmount());
        return budgetRepository.save(budget);
    }

    @Override
    public void deleteBudget(Long id) {
        Budget budget = getBudgetById(id);
        budgetRepository.delete(budget);
    }
}