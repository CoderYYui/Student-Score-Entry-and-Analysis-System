package com.student.score.system.mapper;

import com.student.score.system.entity.SatisfactionSurvey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SatisfactionSurveyMapper {
    @Insert("INSERT INTO satisfaction_surveys (student_id, course_id, satisfaction_level, comment) VALUES (#{studentId}, #{courseId}, #{satisfactionLevel}, #{comment})")
    void insert(SatisfactionSurvey survey);

    @Select("SELECT * FROM satisfaction_surveys WHERE course_id = #{courseId}")
    List<SatisfactionSurvey> findByCourseId(Long courseId);

    @Select("SELECT * FROM satisfaction_surveys WHERE student_id = #{studentId}")
    List<SatisfactionSurvey> findByStudentId(Long studentId);

    @Select("SELECT * FROM satisfaction_surveys WHERE student_id = #{studentId} AND course_id = #{courseId} ORDER BY id DESC LIMIT 1")
    SatisfactionSurvey findLatestByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
