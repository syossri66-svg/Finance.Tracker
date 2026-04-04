package FinanceTracker.com.demo.services;


import FinanceTracker.com.demo.dto.BudgetDto;
import FinanceTracker.com.demo.entities.Budget;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {


    private final List<Budget> budgets = new ArrayList<>();

    @Override
    public Budget createBudget(BudgetDto budgetDto) {
        Budget budget = new Budget();
        budget.setName(budgetDto.getName());
        budget.setStartDate(budgetDto.getStartDate());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setMaxAmount(budgetDto.getMaxAmount());
        budgets.add(budget);
        return budget;
    }

    @Override
    public List<Budget> getAllUserBudgets() {
        return budgets;
    }

    @Override
    public Budget getBudgetById(Long id) {
        return budgets.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Budget updateBudget(Long id, BudgetDto budgetDto) {
        Budget budget = getBudgetById(id);
        if (budget != null) {
            budget.setName(budgetDto.getName());
            budget.setStartDate(budgetDto.getStartDate());
            budget.setEndDate(budgetDto.getEndDate());
            budget.setMaxAmount(budgetDto.getMaxAmount());
        }
        return budget;
    }

    @Override
    public void deleteBudget(Long id) {
        budgets.removeIf(b -> b.getId().equals(id));
    }
}