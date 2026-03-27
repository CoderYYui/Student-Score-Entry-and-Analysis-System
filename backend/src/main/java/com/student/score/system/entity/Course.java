package com.student.score.system.entity;

import lombok.Data;

@Data
public class Course {
    private Long id;
    private String courseCode;
    private String courseName;
    private String semester;
    private Long teacherId;
    
    // New fields for scheduling
    private String classTime;
    private String location;
}
