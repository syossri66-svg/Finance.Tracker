package FinanceTracker.com.demo.services;


import FinanceTracker.com.demo.dto.BudgetDto;
import FinanceTracker.com.demo.entities.Budget;

import java.util.List;

public interface BudgetService {


    Budget createBudget(BudgetDto budgetDto);

    List<Budget> getAllUserBudgets();


    Budget getBudgetById(Long id);

    Budget updateBudget(Long id, BudgetDto budgetDto);

    void deleteBudget(Long id);
}