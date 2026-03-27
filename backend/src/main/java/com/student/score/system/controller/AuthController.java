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
            response.put("message", "用户名已存在");
            return response;
        }
        if (user.getName() == null || user.getName().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("success", false);
            response.put("message", "请完整填写注册信息");
            return response;
        }
        
        user.setStatus("PENDING");
        userMapper.insert(user);
        response.put("success", true);
        response.put("message", "注册申请已提交，请等待教学秘书审批");
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
               response.put("message", "用户不存在");
             return response;
        }

        if (user.isLocked()) {
             response.put("success", false);
               response.put("message", "账号已锁定，请联系管理员处理");
             return response;
        }

        if ("PENDING".equals(user.getStatus())) {
             response.put("success", false);
               response.put("message", "账号尚未审批，请联系教学秘书");
             return response;
        }

        if ("REJECTED".equals(user.getStatus())) {
             response.put("success", false);
               response.put("message", "注册申请已驳回，请重新提交");
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
                response.put("message", "连续 5 次密码错误，账号已锁定");
            } else {
                response.put("message", "无效凭证，已失败 " + user.getFailedAttempts() + " 次");
            }
        }
        return response;
    }
}
