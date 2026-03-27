package com.student.score.system.mapper;

import com.student.score.system.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisMapper {

    @Select("""
        SELECT 
            CASE 
                WHEN total_score >= 90 THEN '90-100'
                WHEN total_score >= 80 THEN '80-89'
                WHEN total_score >= 70 THEN '70-79'
                WHEN total_score >= 60 THEN '60-69'
                ELSE '0-59'
            END as score_range,
            COUNT(*) as student_count,
            CAST(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM scores WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL) AS DECIMAL(10,2)) as percentage
        FROM scores
        WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL
        GROUP BY 
            CASE 
                WHEN total_score >= 90 THEN '90-100'
                WHEN total_score >= 80 THEN '80-89'
                WHEN total_score >= 70 THEN '70-79'
                WHEN total_score >= 60 THEN '60-69'
                ELSE '0-59'
            END
    """)
    List<Map<String, Object>> getScoreDistribution(Long courseId);

    @Select("""
        SELECT 
            'Final Exam' as assessment_method,
            SUM(CASE WHEN final_exam_score >= 90 THEN 1 ELSE 0 END) as excellent,
            SUM(CASE WHEN final_exam_score >= 80 AND final_exam_score < 90 THEN 1 ELSE 0 END) as good,
            SUM(CASE WHEN final_exam_score >= 70 AND final_exam_score < 80 THEN 1 ELSE 0 END) as medium,
            SUM(CASE WHEN final_exam_score >= 60 AND final_exam_score < 70 THEN 1 ELSE 0 END) as pass,
            SUM(CASE WHEN final_exam_score < 60 THEN 1 ELSE 0 END) as fail
        FROM scores WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL
        UNION ALL
        SELECT 
            'Lab' as assessment_method,
            SUM(CASE WHEN lab_aggregate >= 90 THEN 1 ELSE 0 END) as excellent,
            SUM(CASE WHEN lab_aggregate >= 80 AND lab_aggregate < 90 THEN 1 ELSE 0 END) as good,
            SUM(CASE WHEN lab_aggregate >= 70 AND lab_aggregate < 80 THEN 1 ELSE 0 END) as medium,
            SUM(CASE WHEN lab_aggregate >= 60 AND lab_aggregate < 70 THEN 1 ELSE 0 END) as pass,
            SUM(CASE WHEN lab_aggregate < 60 THEN 1 ELSE 0 END) as fail
        FROM scores WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL
        UNION ALL
        SELECT 
            'Tests' as assessment_method,
            SUM(CASE WHEN test_aggregate >= 90 THEN 1 ELSE 0 END) as excellent,
            SUM(CASE WHEN test_aggregate >= 80 AND test_aggregate < 90 THEN 1 ELSE 0 END) as good,
            SUM(CASE WHEN test_aggregate >= 70 AND test_aggregate < 80 THEN 1 ELSE 0 END) as medium,
            SUM(CASE WHEN test_aggregate >= 60 AND test_aggregate < 70 THEN 1 ELSE 0 END) as pass,
            SUM(CASE WHEN test_aggregate < 60 THEN 1 ELSE 0 END) as fail
        FROM scores WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL
        UNION ALL
        SELECT 
            'Regular' as assessment_method,
            SUM(CASE WHEN regular_aggregate >= 90 THEN 1 ELSE 0 END) as excellent,
            SUM(CASE WHEN regular_aggregate >= 80 AND regular_aggregate < 90 THEN 1 ELSE 0 END) as good,
            SUM(CASE WHEN regular_aggregate >= 70 AND regular_aggregate < 80 THEN 1 ELSE 0 END) as medium,
            SUM(CASE WHEN regular_aggregate >= 60 AND regular_aggregate < 70 THEN 1 ELSE 0 END) as pass,
            SUM(CASE WHEN regular_aggregate < 60 THEN 1 ELSE 0 END) as fail
        FROM scores WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL AND total_score IS NOT NULL
    """)
    List<Map<String, Object>> getDirectEvaluationDistribution(Long courseId);

    @Select("""
        SELECT 
            COUNT(*) as total_students,
            MAX(total_score) as max_score,
            MIN(total_score) as min_score,
            AVG(total_score) as avg_score,
            STDDEV_POP(total_score) as std_dev
        FROM scores
        WHERE course_id = #{courseId} AND total_score IS NOT NULL AND total_score IS NOT NULL
    """)
    Map<String, Object> getCourseProtectStats(Long courseId);
}
