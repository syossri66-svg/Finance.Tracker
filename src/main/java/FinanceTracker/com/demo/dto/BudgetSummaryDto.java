package FinanceTracker.com.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BudgetSummaryDto {
    private Long budgetId;
    private String budgetName;
    private String categoryName;
    private BigDecimal budgetAmount;
    private BigDecimal actualSpend;
    private BigDecimal remaining;
    private double percentageUsed;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isOverBudget;
}