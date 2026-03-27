package com.student.score.system.controller;

import com.student.score.system.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/warnings")
public class WarningController {

    private final AnalysisService analysisService;

    public WarningController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/course/{courseId}")
    public Object getCourseWarnings(@PathVariable Long courseId) {
        return analysisService.getCourseWarnings(courseId);
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public Object getStudentWarningDetail(@PathVariable Long courseId, @PathVariable Long studentId) {
        var result = analysisService.getStudentWarningDetail(courseId, studentId);
        if (result == null) {
            return Map.of("warning", false, "message", "暂无预警学生");
        }
        return result;
    }

    @GetMapping("/student/{studentId}")
    public Object getStudentWarnings(@PathVariable Long studentId) {
        return analysisService.getStudentWarningSummary(studentId);
    }
}