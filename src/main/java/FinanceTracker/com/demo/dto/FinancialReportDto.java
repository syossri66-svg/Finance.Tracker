package FinanceTracker.com.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FinancialReportDto {
    private LocalDate reportDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;

    private BigDecimal totalCurrentBalance;

    private List<BudgetSummaryDto> budgetSummaries;

}