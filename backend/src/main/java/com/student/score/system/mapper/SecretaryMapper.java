package com.student.score.system.mapper;

import com.student.score.system.entity.Course;
import com.student.score.system.entity.CourseObjective;
import com.student.score.system.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SecretaryMapper {
    @Insert("INSERT INTO courses (course_code, course_name, semester, teacher_id, class_time, location) VALUES (#{courseCode}, #{courseName}, #{semester}, #{teacherId}, #{classTime}, #{location})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCourse(Course course);

    @Select("SELECT * FROM users WHERE role = 'TEACHER'")
    List<User> findAllTeachers();
    
    @Insert("INSERT INTO course_objectives (course_id, objective_index, description, regular_weight, test_weight, lab_weight, final_weight) VALUES (#{courseId}, #{objectiveIndex}, #{description}, #{regularWeight}, #{testWeight}, #{labWeight}, #{finalWeight})")
    void insertObjective(CourseObjective objective);

    @Select("SELECT * FROM courses")
    List<Course> findAllCourses();
    
    @Insert("INSERT INTO enrollments (student_id, course_id) VALUES (#{studentId}, #{courseId})")
    void enrollStudent(Long studentId, Long courseId);
}
