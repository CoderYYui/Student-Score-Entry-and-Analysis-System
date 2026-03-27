package com.student.score.system.controller;

import com.student.score.system.entity.SatisfactionSurvey;
import com.student.score.system.mapper.SatisfactionSurveyMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    private final SatisfactionSurveyMapper surveyMapper;

    public SurveyController(SatisfactionSurveyMapper surveyMapper) {
        this.surveyMapper = surveyMapper;
    }

    @PostMapping("/submit")
    public Map<String, Object> submitSurvey(@RequestBody SatisfactionSurvey survey) {
        surveyMapper.insert(survey);
        return Map.of("success", true, "message", "Survey submitted successfully");
    }
    
    @GetMapping("/course/{courseId}")
    public List<SatisfactionSurvey> getCourseSurveys(@PathVariable Long courseId) {
        return surveyMapper.findByCourseId(courseId);
    }
}
