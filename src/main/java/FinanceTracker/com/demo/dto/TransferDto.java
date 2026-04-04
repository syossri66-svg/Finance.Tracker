package FinanceTracker.com.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransferDto {

    @NotNull(message = "Source Account ID is required")
    private Long sourceAccountId;

    @NotNull(message = "Destination Account ID is required")
    private Long destinationAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String notes;

    @NotNull(message = "Transfer date is required")
    private LocalDateTime transferDate;
}