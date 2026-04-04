package FinanceTracker.com.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String type;
    private String color;
    private Long userId;
    private LocalDateTime created_at;
}