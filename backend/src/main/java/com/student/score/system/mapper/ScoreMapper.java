package com.student.score.system.mapper;

import com.student.score.system.entity.Score;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreMapper {
    @Select("SELECT s.*, u.name as studentName FROM scores s JOIN users u ON s.student_id = u.id WHERE s.course_id = #{courseId}")
    List<Score> findByCourseId(Long courseId);

    @Select("SELECT s.*, u.name as studentName FROM scores s JOIN users u ON s.student_id = u.id WHERE s.course_id = #{courseId} AND s.student_id = #{studentId}")
    Score findByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("SELECT s.*, u.name as studentName FROM scores s JOIN users u ON s.student_id = u.id WHERE s.student_id = #{studentId}")
    List<Score> findByStudentId(Long studentId);

    @Delete("DELETE FROM scores WHERE course_id = #{courseId}")
    void deleteByCourseId(Long courseId);

    @Update("UPDATE scores SET " +
            "attendance_score=#{attendanceScore}, performance_score=#{performanceScore}, homework_score=#{homeworkScore}, " +
            "test1_score=#{test1Score}, test2_score=#{test2Score}, test3_score=#{test3Score}, test4_score=#{test4Score}, " +
            "lab_attendance_score=#{labAttendanceScore}, lab_report_score=#{labReportScore}, final_exam_score=#{finalExamScore}, absence_count=#{absenceCount}, " +
            "regular_aggregate=#{regularAggregate}, test_aggregate=#{testAggregate}, lab_aggregate=#{labAggregate}, total_score=#{totalScore}, grade_label=#{gradeLabel} " +
            "WHERE id=#{id}")
    void update(Score score);
    
    @Insert("INSERT INTO scores (student_id, course_id, attendance_score, performance_score, homework_score, " +
            "test1_score, test2_score, test3_score, test4_score, lab_attendance_score, lab_report_score, final_exam_score, absence_count, " +
            "regular_aggregate, test_aggregate, lab_aggregate, total_score, grade_label) " +
            "VALUES (#{studentId}, #{courseId}, #{attendanceScore}, #{performanceScore}, #{homeworkScore}, " +
            "#{test1Score}, #{test2Score}, #{test3Score}, #{test4Score}, #{labAttendanceScore}, #{labReportScore}, #{finalExamScore}, #{absenceCount}, " +
            "#{regularAggregate}, #{testAggregate}, #{labAggregate}, #{totalScore}, #{gradeLabel})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Score score);
}
