package com.student.score.system.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String name;
    private String email;
    
    // Security fields
    private int failedAttempts = 0;
    private boolean locked = false;
    
    // Approval status
    private String status = "APPROVED"; // PENDING, APPROVED, REJECTED
}
