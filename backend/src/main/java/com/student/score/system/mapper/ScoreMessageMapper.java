package com.student.score.system.mapper;

import com.student.score.system.entity.ScoreMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ScoreMessageMapper {
    @Insert("INSERT INTO score_messages(course_id, student_id, teacher_id, question_text, question_attachment, reply_text, reply_attachment, status) VALUES(#{courseId}, #{studentId}, #{teacherId}, #{questionText}, #{questionAttachment}, #{replyText}, #{replyAttachment}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ScoreMessage message);

    @Update("UPDATE score_messages SET reply_text = #{replyText}, reply_attachment = #{replyAttachment}, status = #{status}, replied_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void reply(ScoreMessage message);

    @Select("SELECT m.*, s.name AS studentName, t.name AS teacherName, c.course_name AS courseName FROM score_messages m JOIN users s ON m.student_id = s.id JOIN users t ON m.teacher_id = t.id JOIN courses c ON m.course_id = c.id WHERE m.course_id = #{courseId} AND m.student_id = #{studentId} ORDER BY m.created_at DESC")
    List<ScoreMessage> findByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Select("SELECT m.*, s.name AS studentName, t.name AS teacherName, c.course_name AS courseName FROM score_messages m JOIN users s ON m.student_id = s.id JOIN users t ON m.teacher_id = t.id JOIN courses c ON m.course_id = c.id WHERE m.course_id = #{courseId} AND m.teacher_id = #{teacherId} ORDER BY CASE WHEN m.status = 'PENDING' THEN 0 ELSE 1 END, m.created_at DESC")
    List<ScoreMessage> findByCourseAndTeacher(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    @Select("SELECT m.*, s.name AS studentName, t.name AS teacherName, c.course_name AS courseName FROM score_messages m JOIN users s ON m.student_id = s.id JOIN users t ON m.teacher_id = t.id JOIN courses c ON m.course_id = c.id WHERE m.student_id = #{studentId} ORDER BY m.created_at DESC")
    List<ScoreMessage> findByStudentId(Long studentId);

    @Select("SELECT m.*, s.name AS studentName, t.name AS teacherName, c.course_name AS courseName FROM score_messages m JOIN users s ON m.student_id = s.id JOIN users t ON m.teacher_id = t.id JOIN courses c ON m.course_id = c.id WHERE m.id = #{id}")
    ScoreMessage findById(Long id);
}