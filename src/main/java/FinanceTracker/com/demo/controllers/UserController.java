package FinanceTracker.com.demo.controllers;

import FinanceTracker.com.demo.dto.UserCreationDto;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserCreationDto userDto) {
        try {
            User newUser = userService.registerNewUser(userDto);
            return new ResponseEntity<>("User registered successfully! ID: " + newUser.getId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}