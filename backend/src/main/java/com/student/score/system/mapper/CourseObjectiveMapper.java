package com.student.score.system.mapper;

import com.student.score.system.entity.CourseObjective;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseObjectiveMapper {
    @Select("SELECT * FROM course_objectives WHERE course_id = #{courseId}")
    List<CourseObjective> findByCourseId(Long courseId);
}
