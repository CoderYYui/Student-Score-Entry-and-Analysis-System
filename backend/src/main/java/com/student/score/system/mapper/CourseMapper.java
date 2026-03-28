package com.student.score.system.mapper;

import com.student.score.system.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("SELECT * FROM courses")
    List<Course> findAll();

    @Select("SELECT * FROM courses WHERE teacher_id = #{teacherId}")
    List<Course> findByTeacherId(Long teacherId);

    @Select("SELECT c.* FROM courses c JOIN enrollments e ON c.id = e.course_id WHERE e.student_id = #{studentId}")
    List<Course> findByStudentId(Long studentId);

    @Select("SELECT * FROM courses WHERE id = #{id}")
    Course findById(Long id);

    @Insert("INSERT INTO courses(course_code, course_name, semester, teacher_id, class_time, location) VALUES(#{courseCode}, #{courseName}, #{semester}, #{teacherId}, #{classTime}, #{location})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Course course);

    @Update("UPDATE courses SET course_code = #{courseCode}, course_name = #{courseName}, semester = #{semester}, teacher_id = #{teacherId}, class_time = #{classTime}, location = #{location} WHERE id = #{id}")
    void update(Course course);

    @Delete("DELETE FROM courses WHERE id = #{id}")
    void delete(Long id);

    @Select("SELECT student_id FROM enrollments WHERE course_id = #{courseId}")
    List<Long> findStudentIdsByCourseId(Long courseId);

    @Insert("INSERT INTO enrollments(course_id, student_id) VALUES(#{courseId}, #{studentId})")
    void addEnrollment(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Delete("DELETE FROM enrollments WHERE course_id = #{courseId} AND student_id = #{studentId}")
    void removeEnrollment(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    @Delete("DELETE FROM enrollments WHERE course_id = #{courseId}")
    void removeAllEnrollments(Long courseId);

    @Delete("DELETE FROM course_objectives WHERE course_id = #{courseId}")
    void removeAllObjectivesByCourseId(Long courseId);

    @Delete("DELETE FROM scores WHERE course_id = #{courseId}")
    void removeAllScoresByCourseId(Long courseId);

    @Delete("DELETE FROM satisfaction_surveys WHERE course_id = #{courseId}")
    void removeAllSurveysByCourseId(Long courseId);

    @Delete("DELETE FROM score_messages WHERE course_id = #{courseId}")
    void removeAllMessagesByCourseId(Long courseId);
}
