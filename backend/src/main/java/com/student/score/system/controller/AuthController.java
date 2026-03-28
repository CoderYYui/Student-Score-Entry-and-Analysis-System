package com.student.score.system.controller;

import com.student.score.system.entity.User;
import com.student.score.system.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        if (userMapper.findByUsername(user.getUsername()) != null) {
            response.put("success", false);
            response.put("message", "Username already exists.");
            return response;
        }
        if (user.getName() == null || user.getName().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("success", false);
            response.put("message", "Please complete the registration information completely.");
            return response;
        }
        
        user.setStatus("PENDING");
        userMapper.insert(user);
        response.put("success", true);
        response.put("message", "Your registration application has been submitted. Please wait for the approval from the teaching secretary.");
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        User user = userMapper.findByUsername(username);
        
        Map<String, Object> response = new HashMap<>();
        
        if (user == null) {
             response.put("success", false);
               response.put("message", "The user does not exist.");
             return response;
        }

        if (user.isLocked()) {
             response.put("success", false);
               response.put("message", "The account has been locked. Please contact the administrator for handling.");
             return response;
        }

        if ("PENDING".equals(user.getStatus())) {
             response.put("success", false);
               response.put("message", "The account has not been approved yet. Please contact the teaching secretary.");
             return response;
        }

        if ("REJECTED".equals(user.getStatus())) {
             response.put("success", false);
               response.put("message", "Your registration application has been rejected. Please resubmit.");
             return response;
        }

        if (user.getPassword().equals(password)) {
            // Reset attempts on success
            if (user.getFailedAttempts() > 0) {
                user.setFailedAttempts(0);
                userMapper.update(user);
            }
            
            response.put("success", true);
            response.put("token", "fake-jwt-token-" + user.getUsername()); 
            response.put("user", user);
        } else {
            // Increment attempts
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setLocked(true);
            }
            userMapper.update(user);
            
            response.put("success", false);
            if (user.isLocked()) {
                response.put("message", "Five consecutive incorrect password entries have been made, and the account has been locked.");
            } else {
                response.put("message", "Invalid voucher, failed " + user.getFailedAttempts() + " times");
            }
        }
        return response;
    }
}
