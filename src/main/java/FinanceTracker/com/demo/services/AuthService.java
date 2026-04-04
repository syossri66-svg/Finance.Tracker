package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.JwtResponseDto;
import FinanceTracker.com.demo.dto.LoginDto;
import FinanceTracker.com.demo.dto.RegisterDto;


import FinanceTracker.com.demo.dto.JwtResponseDto;
import FinanceTracker.com.demo.dto.LoginDto;
import FinanceTracker.com.demo.dto.RegisterDto;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.exception.FinanceTrackerAPIException;
import FinanceTracker.com.demo.repositories.UserRepository;
import FinanceTracker.com.demo.utities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public void register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new FinanceTrackerAPIException(HttpStatus.BAD_REQUEST, "Username already exists.");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new FinanceTrackerAPIException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFullName(registerDto.getFullName());
        userRepository.save(user);
    }

    public JwtResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateToken(authentication);

        return new JwtResponseDto(token, user.getId(), user.getUsername(), user.getEmail());
    }
}