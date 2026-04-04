package FinanceTracker.com.demo.dto;

import org.springframework.security.core.Authentication;

public class JwtResponseDto {
    public static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.OAuth2ClientMutator builder() {
        return null;
    }

    public String generateToken(Authentication authentication) {
        return "";
    }
}
