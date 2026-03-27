package com.student.score.system.entity;

import lombok.Data;

@Data
public class Score {
    private Long id;
    private Long studentId;
    private Long courseId;
    
    private Double attendanceScore;
    private Double performanceScore;
    private Double homeworkScore;
    
    private Double test1Score;
    private Double test2Score;
    private Double test3Score;
    private Double test4Score;
    
    private Double labAttendanceScore;
    private Double labReportScore;
    
    private Double finalExamScore;
    private Integer absenceCount;
    
    // Transient field for UI display
    private String studentName;
    
    // Aggregates
    private Double regularAggregate;
    private Double testAggregate;
    private Double labAggregate; // 0.2 * labAttendance + 0.8 * labReport
    private Double totalScore;
    private String gradeLabel;

    // Helper to calculate totals
    public void calculate() {
        this.regularAggregate = (0.3 * (attendanceScore == null ? 0 : attendanceScore)) 
                              + (0.2 * (performanceScore == null ? 0 : performanceScore)) 
                              + (0.5 * (homeworkScore == null ? 0 : homeworkScore));
        
        double t1 = test1Score == null ? 0 : test1Score;
        double t2 = test2Score == null ? 0 : test2Score;
        double t3 = test3Score == null ? 0 : test3Score;
        double t4 = test4Score == null ? 0 : test4Score;
        this.testAggregate = (t1 + t2 + t3 + t4) / 4.0;
        
        this.labAggregate = (0.2 * (labAttendanceScore == null ? 0 : labAttendanceScore))
                          + (0.8 * (labReportScore == null ? 0 : labReportScore));
                          
        this.totalScore = (0.2 * regularAggregate) + (0.2 * testAggregate) 
                        + (0.3 * labAggregate) + (0.3 * (finalExamScore == null ? 0 : finalExamScore));
                        
        if (this.totalScore >= 90) this.gradeLabel = "优秀";
        else if (this.totalScore >= 80) this.gradeLabel = "良好";
        else if (this.totalScore >= 70) this.gradeLabel = "中等";
        else if (this.totalScore >= 60) this.gradeLabel = "及格";
        else this.gradeLabel = "不及格";
    }
}
