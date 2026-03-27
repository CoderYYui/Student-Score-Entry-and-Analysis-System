package com.student.score.system.entity;

import lombok.Data;

import java.util.List;

@Data
public class WarningRecord {
    private Long courseId;
    private String courseName;
    private Long studentId;
    private String studentName;
    private String username;
    private Double regularAggregate;
    private Double totalScore;
    private Double finalExamScore;
    private Integer absenceCount;
    private List<String> riskItems;
    // private String emotionLabel;
    // private String emotionDetail;
    // private String communicationAdvice;
}