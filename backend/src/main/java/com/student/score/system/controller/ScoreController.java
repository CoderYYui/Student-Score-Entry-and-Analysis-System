package com.student.score.system.controller;

import com.student.score.system.entity.Score;
import com.student.score.system.mapper.ScoreMapper;
import com.student.score.system.mapper.CourseMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreMapper scoreMapper;
    private final CourseMapper courseMapper;

    public ScoreController(ScoreMapper scoreMapper, CourseMapper courseMapper) {
        this.scoreMapper = scoreMapper;
        this.courseMapper = courseMapper;
    }

    @GetMapping("/course/{courseId}")
    public List<Score> getScoresByCourse(@PathVariable Long courseId) {
        return scoreMapper.findByCourseId(courseId);
    }

    @PostMapping("/course/{courseId}/reset")
    @Transactional
    public ResponseEntity<?> resetScores(@PathVariable Long courseId) {
        // 1) 删除该课程所有成绩记录（清空）
        scoreMapper.deleteByCourseId(courseId);

        // 2) 按当前选课名单重新插入空白行
        List<Long> enrolledStudentIds = courseMapper.findStudentIdsByCourseId(courseId);
        for (Long sId : enrolledStudentIds) {
            Score newScore = new Score();
            newScore.setCourseId(courseId);
            newScore.setStudentId(sId);
            scoreMapper.insert(newScore);
        }

        return ResponseEntity.ok(Map.of("message", "Scores reset"));
    }

    @PostMapping("/course/{courseId}/init")
    public ResponseEntity<?> initMissingScores(@PathVariable Long courseId) {
        List<Long> enrolledStudentIds = courseMapper.findStudentIdsByCourseId(courseId);
        List<Score> existingScores = scoreMapper.findByCourseId(courseId);
        
        for (Long sId : enrolledStudentIds) {
            boolean hasScore = existingScores.stream().anyMatch(s -> sId.equals(s.getStudentId()));
            if (!hasScore) {
                Score newScore = new Score();
                newScore.setCourseId(courseId);
                newScore.setStudentId(sId);
                scoreMapper.insert(newScore);
            }
        }
        return ResponseEntity.ok(Map.of("message", "Scores initialized"));
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public Score getStudentScore(@PathVariable Long courseId, @PathVariable Long studentId) {
        return scoreMapper.findByStudentAndCourse(studentId, courseId);
    }

    @PostMapping("/update")
    public Score updateScore(@RequestBody Score score) {
        // Recalculate Logic
        score.calculate();

        if (score.getId() != null) {
            scoreMapper.update(score);
        } else {
            scoreMapper.insert(score);
        }
        return score;
    }
}
