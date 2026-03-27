package com.student.score.system.entity;

import lombok.Data;

@Data
public class SatisfactionSurvey {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String satisfactionLevel; // VERY_SATISFIED, SATISFIED, etc.
    private String comment;
}
