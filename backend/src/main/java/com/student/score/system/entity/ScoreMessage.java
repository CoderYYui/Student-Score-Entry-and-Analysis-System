package com.student.score.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScoreMessage {
    private Long id;
    private Long courseId;
    private Long studentId;
    private Long teacherId;
    private String questionText;
    private String questionAttachment;
    private String replyText;
    private String replyAttachment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime repliedAt;
    private String studentName;
    private String teacherName;
    private String courseName;
}