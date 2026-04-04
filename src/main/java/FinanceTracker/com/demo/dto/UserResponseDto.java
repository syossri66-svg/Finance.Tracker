package FinanceTracker.com.demo.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
    public class UserResponseDto {

        private Long id;

        private String username;

        private String email;

        private String fullName;

        private Boolean is_admin;

        private LocalDateTime created_at;

        private LocalDateTime updated_at;
    }

