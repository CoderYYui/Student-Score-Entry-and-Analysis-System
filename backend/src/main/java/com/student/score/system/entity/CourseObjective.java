package com.student.score.system.entity;

import lombok.Data;

@Data
public class CourseObjective {
    private Long id;
    private Long courseId;
    private Integer objectiveIndex;
    private String description;
    
    // Weights for components (0.0 - 1.0)
    private Double regularWeight;
    private Double testWeight;
    private Double labWeight;
    private Double finalWeight;
}
