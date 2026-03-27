package com.student.score.system.controller;

import com.student.score.system.entity.ScoreMessage;
import com.student.score.system.mapper.ScoreMessageMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final ScoreMessageMapper scoreMessageMapper;

    public MessageController(ScoreMessageMapper scoreMessageMapper) {
        this.scoreMessageMapper = scoreMessageMapper;
    }

    @PostMapping("/question")
    public Map<String, Object> submitQuestion(@RequestBody ScoreMessage message) {
        message.setStatus("PENDING");
        scoreMessageMapper.insert(message);
        return Map.of("success", true, "message", "提问提交成功", "data", message);
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public List<ScoreMessage> getStudentMessages(@PathVariable Long courseId, @PathVariable Long studentId) {
        return scoreMessageMapper.findByCourseAndStudent(courseId, studentId);
    }

    @GetMapping("/course/{courseId}/teacher/{teacherId}")
    public List<ScoreMessage> getTeacherMessages(@PathVariable Long courseId, @PathVariable Long teacherId) {
        return scoreMessageMapper.findByCourseAndTeacher(courseId, teacherId);
    }

    @PostMapping("/{id}/reply")
    public Map<String, Object> reply(@PathVariable Long id, @RequestBody ScoreMessage payload) {
        ScoreMessage current = scoreMessageMapper.findById(id);
        if (current == null) {
            return Map.of("success", false, "message", "留言不存在");
        }
        current.setReplyText(payload.getReplyText());
        current.setReplyAttachment(payload.getReplyAttachment());
        current.setStatus("REPLIED");
        scoreMessageMapper.reply(current);
        return Map.of("success", true, "message", "回复成功发送");
    }
}