package com.student.score.system.controller;

import com.student.score.system.entity.Course;
import com.student.score.system.mapper.CourseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseMapper courseMapper;

    public CourseController(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseMapper.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseMapper.findById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        courseMapper.insert(course);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        courseMapper.update(course);
        return ResponseEntity.ok(course);
    }



    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseMapper.removeAllMessagesByCourseId(id);
        courseMapper.removeAllSurveysByCourseId(id);
        courseMapper.removeAllScoresByCourseId(id);
        courseMapper.removeAllObjectivesByCourseId(id);
        courseMapper.removeAllEnrollments(id);
        courseMapper.delete(id);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/teacher/{teacherId}")
    public List<Course> getTeacherCourses(@PathVariable Long teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }

    @GetMapping("/student/{studentId}")
    public List<Course> getStudentCourses(@PathVariable Long studentId) {
        return courseMapper.findByStudentId(studentId);
    }

    // Bindings API
    @GetMapping("/{courseId}/students")
    public List<Long> getEnrollments(@PathVariable Long courseId) {
        return courseMapper.findStudentIdsByCourseId(courseId);
    }

    @PostMapping("/{courseId}/students")
    public ResponseEntity<?> configureEnrollments(@PathVariable Long courseId, @RequestBody Map<String, List<Long>> payload) {
        List<Long> studentIds = payload.get("studentIds");
        courseMapper.removeAllEnrollments(courseId);
        if (studentIds != null) {
            for (Long studentId : studentIds) {
                courseMapper.addEnrollment(courseId, studentId);
            }
        }
        return ResponseEntity.ok().build();
    }
}
