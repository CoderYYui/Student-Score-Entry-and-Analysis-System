package com.student.score.system.controller;

import com.student.score.system.entity.Course;
import com.student.score.system.entity.CourseObjective;
import com.student.score.system.entity.User;
import com.student.score.system.mapper.SecretaryMapper;
import com.student.score.system.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/secretary")
public class SecretaryController {

    private final SecretaryMapper secretaryMapper;
    private final UserMapper userMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SecretaryController(SecretaryMapper secretaryMapper, UserMapper userMapper) {
        this.secretaryMapper = secretaryMapper;
        this.userMapper = userMapper;
    }

    @PostMapping("/course/create")
    public Map<String, Object> createCourse(@RequestBody Course course) {
        secretaryMapper.insertCourse(course);
        return Map.of("success", true, "message", "Course created successfully", "courseId", course.getId());
    }

    @GetMapping("/teachers")
    public List<User> getAllTeachers() {
        return userMapper.findByRole("TEACHER");
    }

    @GetMapping("/students")
    public List<User> getAllStudents() {
        return userMapper.findByRole("STUDENT");
    }

    @PostMapping("/teachers")
    public Map<String, Object> createTeacher(@RequestBody User user) {
        return createUserByRole(user, "TEACHER");
    }

    @PostMapping("/students")
    public Map<String, Object> createStudent(@RequestBody User user) {
        return createUserByRole(user, "STUDENT");
    }

    @PutMapping("/teachers/{id}")
    public Map<String, Object> updateTeacher(@PathVariable Long id, @RequestBody User payload) {
        return updateUserByRole(id, payload, "TEACHER");
    }

    @PutMapping("/students/{id}")
    public Map<String, Object> updateStudent(@PathVariable Long id, @RequestBody User payload) {
        return updateUserByRole(id, payload, "STUDENT");
    }

    @DeleteMapping("/teachers/{id}")
    public Map<String, Object> deleteTeacher(@PathVariable Long id) {
        return deleteUserByRole(id, "TEACHER");
    }

    @DeleteMapping("/students/{id}")
    public Map<String, Object> deleteStudent(@PathVariable Long id) {
        return deleteUserByRole(id, "STUDENT");
    }

    @PostMapping("/course/objective")
    public Map<String, Object> addObjective(@RequestBody CourseObjective objective) {
        secretaryMapper.insertObjective(objective);
        return Map.of("success", true, "message", "Objective added");
    }

    @PostMapping("/enroll")
    public Map<String, Object> enrollStudent(@RequestBody Map<String, Long> payload) {
        secretaryMapper.enrollStudent(payload.get("studentId"), payload.get("courseId"));
        return Map.of("success", true, "message", "Student enrolled");
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return secretaryMapper.findAllCourses();
    }

    @GetMapping("/pending-users")
    public List<Map<String, Object>> getPendingUsers() {
        return jdbcTemplate.queryForList("SELECT id, username, name, email, role, status FROM users WHERE status = 'PENDING'");
    }

    @PostMapping("/approve-user/{id}")
    public Map<String, Object> approveUser(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status"); // APPROVED or REJECTED
        String approvedRole = payload.get("role");
        User user = userMapper.findById(id);
        if (user != null) {
            user.setStatus(newStatus);
            if (approvedRole != null && !approvedRole.isBlank()) {
                user.setRole(approvedRole);
            }
            userMapper.update(user);
            return Map.of("success", true, "message", "审批完成，用户状态已更新", "user", user);
        }
        return Map.of("success", false, "message", "用户不存在");
    }

    private Map<String, Object> createUserByRole(User user, String role) {
        Map<String, Object> response = new HashMap<>();
        if (user.getUsername() == null || user.getUsername().isBlank()
            || user.getName() == null || user.getName().isBlank()
            || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("success", false);
            response.put("message", "请完整填写用户信息");
            return response;
        }
        if (userMapper.findByUsername(user.getUsername()) != null) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }
        user.setRole(role);
        user.setStatus("APPROVED");
        user.setFailedAttempts(0);
        user.setLocked(false);
        userMapper.insert(user);
        response.put("success", true);
        response.put("message", "用户创建成功");
        response.put("user", user);
        return response;
    }

    private Map<String, Object> updateUserByRole(Long id, User payload, String role) {
        Map<String, Object> response = new HashMap<>();
        User existingUser = userMapper.findById(id);
        if (existingUser == null || !role.equals(existingUser.getRole())) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }
        User duplicateUser = userMapper.findByUsername(payload.getUsername());
        if (duplicateUser != null && !duplicateUser.getId().equals(id)) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }
        existingUser.setUsername(payload.getUsername());
        existingUser.setName(payload.getName());
        existingUser.setEmail(payload.getEmail());
        existingUser.setRole(role);
        existingUser.setStatus(payload.getStatus() == null || payload.getStatus().isBlank() ? existingUser.getStatus() : payload.getStatus());
        if (payload.getPassword() != null && !payload.getPassword().isBlank()) {
            existingUser.setPassword(payload.getPassword());
        }
        userMapper.update(existingUser);
        response.put("success", true);
        response.put("message", "用户更新成功");
        response.put("user", existingUser);
        return response;
    }

    private Map<String, Object> deleteUserByRole(Long id, String role) {
        Map<String, Object> response = new HashMap<>();
        User existingUser = userMapper.findById(id);
        if (existingUser == null || !role.equals(existingUser.getRole())) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }
        userMapper.deleteById(id);
        response.put("success", true);
        response.put("message", "用户删除成功");
        return response;
    }
}
