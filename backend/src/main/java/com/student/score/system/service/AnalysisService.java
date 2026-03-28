package com.student.score.system.service;

import com.student.score.system.entity.CourseObjective;
import com.student.score.system.entity.Score;
import com.student.score.system.entity.ScoreMessage;
import com.student.score.system.entity.SatisfactionSurvey;
import com.student.score.system.entity.WarningRecord;
import com.student.score.system.mapper.CourseMapper;
import com.student.score.system.mapper.CourseObjectiveMapper;
import com.student.score.system.mapper.ScoreMapper;
import com.student.score.system.mapper.ScoreMessageMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final ScoreMapper scoreMapper;
    private final CourseObjectiveMapper objectiveMapper;
    private final com.student.score.system.mapper.AnalysisMapper analysisMapper;
    private final com.student.score.system.mapper.SatisfactionSurveyMapper surveyMapper;
    private final CourseMapper courseMapper;
    private final ScoreMessageMapper messageMapper;

    public AnalysisService(ScoreMapper scoreMapper, CourseObjectiveMapper objectiveMapper, com.student.score.system.mapper.AnalysisMapper analysisMapper, com.student.score.system.mapper.SatisfactionSurveyMapper surveyMapper, CourseMapper courseMapper, ScoreMessageMapper messageMapper) {
        this.scoreMapper = scoreMapper;
        this.objectiveMapper = objectiveMapper;
        this.analysisMapper = analysisMapper;
        this.surveyMapper = surveyMapper;
        this.courseMapper = courseMapper;
        this.messageMapper = messageMapper;
    }

    public Map<String, Object> getFullAnalysis(Long courseId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. Objective Achievement (Average)
        result.put("objectiveAchievement", calculateObjectiveAchievement(courseId));
        
        // 2. Class Score Distribution
        result.put("scoreDistribution", analysisMapper.getScoreDistribution(courseId));
        
        // 3. Direct Evaluation Distribution (Excellent, Good, etc. for each component)
        result.put("directEvaluation", analysisMapper.getDirectEvaluationDistribution(courseId));
        
        // 4. Course Stats (Max, Min, Avg, StdDev)
        result.put("courseStats", analysisMapper.getCourseProtectStats(courseId));
        
        // 5. Objective Achievement Distribution (Individual [0, 0.6), [0.6, 0.7) etc.)
        result.put("objectiveDistribution", calculateObjectiveDistribution(courseId));

        // 6. Indirect Evaluation (Survey Results)
        result.put("indirectEvaluation", calculateIndirectEvaluation(courseId));

        return result;
    }

    private Map<String, Object> calculateObjectiveDistribution(Long courseId) {
        List<Score> scores = scoreMapper.findByCourseId(courseId);
        List<CourseObjective> objectives = objectiveMapper.findByCourseId(courseId);
        double wRegular = 0.2, wTest = 0.2, wLab = 0.3, wFinal = 0.3;
        
        Map<String, Object> distribution = new HashMap<>();

        for (CourseObjective obj : objectives) {
            Map<String, Integer> buckets = new HashMap<>();
            buckets.put("0-0.6", 0);
            buckets.put("0.6-0.7", 0);
            buckets.put("0.7-0.8", 0);
            buckets.put("0.8-0.9", 0);
            buckets.put("0.9-1.0", 0);
            
            double targetValue = (100 * wRegular * obj.getRegularWeight()) +
                                 (100 * wTest * obj.getTestWeight()) +
                                 (100 * wLab * obj.getLabWeight()) +
                                 (100 * wFinal * obj.getFinalWeight());

            for (Score s : scores) {
               double actualValue = (s.getRegularAggregate() != null ? s.getRegularAggregate() : 0) * wRegular * obj.getRegularWeight() +
                                     (s.getTestAggregate() != null ? s.getTestAggregate() : 0) * wTest * obj.getTestWeight() +
                                     (s.getLabAggregate() != null ? s.getLabAggregate() : 0) * wLab * obj.getLabWeight() +
                                     (s.getFinalExamScore() != null ? s.getFinalExamScore() : 0) * wFinal * obj.getFinalWeight();
                
                double achievement = (targetValue != 0) ? (actualValue / targetValue) : 0;
                
                if (achievement < 0.6) buckets.put("0-0.6", buckets.get("0-0.6") + 1);
                else if (achievement < 0.7) buckets.put("0.6-0.7", buckets.get("0.6-0.7") + 1);
                else if (achievement < 0.8) buckets.put("0.7-0.8", buckets.get("0.7-0.8") + 1);
                else if (achievement < 0.9) buckets.put("0.8-0.9", buckets.get("0.8-0.9") + 1);
                else buckets.put("0.9-1.0", buckets.get("0.9-1.0") + 1);
            }
            distribution.put("Objective " + obj.getObjectiveIndex(), buckets);
        }
        return distribution;
    }

    private Map<String, Object> calculateIndirectEvaluation(Long courseId) {
    var surveys = surveyMapper.findByCourseId(courseId);
    Map<String, Integer> counts = new HashMap<>();
    counts.put("VERY_SATISFIED", 0);
    counts.put("SATISFIED", 0);
    counts.put("NEUTRAL", 0);
    counts.put("DISSATISFIED", 0);

    for (var s : surveys) {
        String raw = s.getSatisfactionLevel();
        String level = normalizeSatisfactionLevel(raw);
        if (level == null) continue; // 未识别的值就跳过
        counts.put(level, counts.getOrDefault(level, 0) + 1);
    }

    return Map.of("counts", counts, "total", surveys.size());
}

private String normalizeSatisfactionLevel(String raw) {
    if (raw == null) return null;
    String v = raw.trim();


    if ("非常满意".equals(v)) return "VERY_SATISFIED";
    if ("比较满意".equals(v)) return "SATISFIED";
    if ("满意".equals(v)) return "SATISFIED";
    if ("基本满意".equals(v)) return "NEUTRAL";
    if ("不满意".equals(v)) return "DISSATISFIED";


    if ("VERY_SATISFIED".equalsIgnoreCase(v)) return "VERY_SATISFIED";
    if ("SATISFIED".equalsIgnoreCase(v)) return "SATISFIED";
    if ("NEUTRAL".equalsIgnoreCase(v)) return "NEUTRAL";
    if ("DISSATISFIED".equalsIgnoreCase(v)) return "DISSATISFIED";

    return null;
}

    public Map<String, Object> calculateStudentObjectiveAchievement(Long courseId, Long studentId) {
        Score score = scoreMapper.findByStudentAndCourse(studentId, courseId);
        List<CourseObjective> objectives = objectiveMapper.findByCourseId(courseId);
        double wRegular = 0.2, wTest = 0.2, wLab = 0.3, wFinal = 0.3;

        Map<String, Object> result = new HashMap<>();

        if (score == null) return result;

        for (CourseObjective obj : objectives) {
            double targetValue = (100 * wRegular * obj.getRegularWeight()) +
                                 (100 * wTest * obj.getTestWeight()) +
                                 (100 * wLab * obj.getLabWeight()) +
                                 (100 * wFinal * obj.getFinalWeight());

            double actualValue = (score.getRegularAggregate() != null ? score.getRegularAggregate() : 0) * wRegular * obj.getRegularWeight() +
                                 (score.getTestAggregate() != null ? score.getTestAggregate() : 0) * wTest * obj.getTestWeight() +
                                 (score.getLabAggregate() != null ? score.getLabAggregate() : 0) * wLab * obj.getLabWeight() +
                                 (score.getFinalExamScore() != null ? score.getFinalExamScore() : 0) * wFinal * obj.getFinalWeight();
            
            double achievement = (targetValue != 0) ? (actualValue / targetValue) : 0;
            result.put("objective_" + obj.getObjectiveIndex(), achievement);
        }
        return result;
    }

    public Map<String, Object> calculateObjectiveAchievement(Long courseId) {
        List<Score> scores = scoreMapper.findByCourseId(courseId);
        List<CourseObjective> objectives = objectiveMapper.findByCourseId(courseId);
        
        // Component Weights (Fixed as per requirement)
        double wRegular = 0.2;
        double wTest = 0.2;
        double wLab = 0.3;
        double wFinal = 0.3;

        Map<String, Object> result = new HashMap<>();
        
        for (CourseObjective obj : objectives) {
            double totalAchievementSum = 0;
            // Calculate target value (denominator)
            // Target = 100 * wReg * obj.wReg + 100 * wTest * obj.wTest ...
            double targetValue = (100 * wRegular * obj.getRegularWeight()) +
                                 (100 * wTest * obj.getTestWeight()) +
                                 (100 * wLab * obj.getLabWeight()) +
                                 (100 * wFinal * obj.getFinalWeight());
                                 
            for (Score s : scores) {
                // Calculate actual value for student
                double actualValue = (s.getRegularAggregate() != null ? s.getRegularAggregate() : 0) * wRegular * obj.getRegularWeight() +
                                     (s.getTestAggregate() != null ? s.getTestAggregate() : 0) * wTest * obj.getTestWeight() +
                                     (s.getLabAggregate() != null ? s.getLabAggregate() : 0) * wLab * obj.getLabWeight() +
                                     (s.getFinalExamScore() != null ? s.getFinalExamScore() : 0) * wFinal * obj.getFinalWeight();
                
                // Achievement for this student for this objective
                double achievement = (targetValue != 0) ? (actualValue / targetValue) : 0;
                totalAchievementSum += achievement;
            }
            
            double averageAchievement = (scores.size() > 0) ? (totalAchievementSum / scores.size()) : 0;
            result.put("objective_" + obj.getObjectiveIndex(), averageAchievement);
        }
        
        return result;
    }

    public List<WarningRecord> getCourseWarnings(Long courseId) {
        var course = courseMapper.findById(courseId);
        if (course == null) {
            return List.of();
        }
        List<Score> scores = scoreMapper.findByCourseId(courseId);
        return scores.stream()
                .map(score -> buildWarningRecord(courseId, course.getCourseName(), score))
                .filter(record -> !record.getRiskItems().isEmpty())
                .sorted(Comparator.comparingInt(record -> -record.getRiskItems().size()))
                .collect(Collectors.toList());
    }

    public WarningRecord getStudentWarningDetail(Long courseId, Long studentId) {
        var course = courseMapper.findById(courseId);
        if (course == null) {
            return null;
        }
        Score score = scoreMapper.findByStudentAndCourse(studentId, courseId);
        if (score == null) {
            return null;
        }
        WarningRecord record = buildWarningRecord(courseId, course.getCourseName(), score);
        if (record.getRiskItems().isEmpty()) {
            return null;
        }
        return record;
    }

    public List<WarningRecord> getStudentWarningSummary(Long studentId) {
        return scoreMapper.findByStudentId(studentId).stream()
                .map(score -> {
                    var course = courseMapper.findById(score.getCourseId());
                    return course == null ? null : buildWarningRecord(score.getCourseId(), course.getCourseName(), score);
                })
                .filter(record -> record != null && !record.getRiskItems().isEmpty())
                .sorted(Comparator.comparing(WarningRecord::getCourseId))
                .collect(Collectors.toList());
    }

    private WarningRecord buildWarningRecord(Long courseId, String courseName, Score score) {
        WarningRecord record = new WarningRecord();
        record.setCourseId(courseId);
        record.setCourseName(courseName);
        record.setStudentId(score.getStudentId());
        record.setStudentName(score.getStudentName());
        record.setRegularAggregate(score.getRegularAggregate());
        record.setTotalScore(score.getTotalScore());
        record.setFinalExamScore(score.getFinalExamScore());
        record.setAbsenceCount(score.getAbsenceCount() == null ? 0 : score.getAbsenceCount());

        List<String> riskItems = new ArrayList<>();
        double totalScore = score.getTotalScore() == null ? 0 : score.getTotalScore();
        double regularAggregate = score.getRegularAggregate() == null ? 0 : score.getRegularAggregate();
        double finalExamScore = score.getFinalExamScore() == null ? 0 : score.getFinalExamScore();
        int absenceCount = score.getAbsenceCount() == null ? 0 : score.getAbsenceCount();

        if (totalScore < 60) {
            riskItems.add("The comprehensive score is lower than 60 points");
        }
        if (regularAggregate < 60) {
            riskItems.add("Relatively low usual grades");
        }
        if (finalExamScore < 60) {
            riskItems.add("There is a risk regarding the final grades");
        }
        if (absenceCount >= 3) {
            riskItems.add("The number of absences has reached " + absenceCount);
        }

        record.setRiskItems(riskItems);

        // SatisfactionSurvey survey = surveyMapper.findLatestByStudentAndCourse(score.getStudentId(), courseId);
        // List<ScoreMessage> messages = messageMapper.findByCourseAndStudent(courseId, score.getStudentId());
        // applyEmotionAnalysis(record, survey, messages);
        return record;
    }

    // private void applyEmotionAnalysis(WarningRecord record, SatisfactionSurvey survey, List<ScoreMessage> messages) {
    //     int pressureScore = 0;
    //     if (record.getTotalScore() != null && record.getTotalScore() < 60) {
    //         pressureScore += 2;
    //     }
    //     if (record.getAbsenceCount() != null && record.getAbsenceCount() >= 3) {
    //         pressureScore += 2;
    //     }
    //     if (survey != null) {
    //         String level = normalizeText(survey.getSatisfactionLevel());
    //         if (level.contains("不满意") || level.contains("dissatisfied") || level.contains("basic")) {
    //             pressureScore += 2;
    //         } else if (level.contains("一般") || level.contains("neutral") || level.contains("满意")) {
    //             pressureScore += 1;
    //         }
    //         String comment = normalizeText(survey.getComment());
    //         if (containsNegativeCue(comment)) {
    //             pressureScore += 2;
    //         }
    //     }
    //     for (ScoreMessage message : messages) {
    //         if (containsNegativeCue(normalizeText(message.getQuestionText()))) {
    //             pressureScore += 1;
    //         }
    //     }

    //     if (pressureScore >= 5) {
    //         record.setEmotionLabel("High-pressure warning: Low academic performance + negative communication tone; gentle communication is recommended.");
    //         record.setEmotionDetail("Students have shown obvious stress and frustration in their recent academic performance, satisfaction surveys or Q&A feedback. It is recommended to confirm their emotions first before discussing a remedial plan.");
    //         record.setCommunicationAdvice("It is recommended to adopt a low-pressure communication approach: first affirm the efforts made, then break down the remedial steps. Prioritize handling make-up assignments, absence resolution, and end-of-term reviews.");
    //     } else if (pressureScore >= 3) {
    //         record.setEmotionLabel("Moderate attention: Learning status fluctuates; it is recommended to follow up proactively");
    //         record.setEmotionDetail("Students experience a certain level of academic pressure and may be sensitive to their current academic performance. It is recommended to provide targeted reminders as soon as possible.");
    //         record.setCommunicationAdvice("It is recommended to clearly identify the main areas where points were lost, provide short-term improvement goals, and confirm whether the student requires additional tutoring.");
    //     } else {
    //         record.setEmotionLabel("General attention: Currently focusing mainly on academic reminders");
    //         record.setEmotionDetail("No obvious negative emotional signals have been detected, and the current risks mainly stem from academic performance or attendance itself.");
    //         record.setCommunicationAdvice("Conventional reminder methods can be adopted to urge students to pay attention to subsequent quizzes and attendance.");
    //     }
    // }

    private boolean containsNegativeCue(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return text.contains("不会")
                || text.contains("听不懂")
                || text.contains("挂科")
                || text.contains("焦虑")
                || text.contains("难")
                || text.contains("压力")
                || text.contains("糟")
                || text.contains("崩")
                || text.contains("fail")
                || text.contains("stress")
                || text.contains("depress")
                || text.contains("bad");
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }
}
