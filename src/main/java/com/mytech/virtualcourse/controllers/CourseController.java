package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.services.CourseService;
import com.mytech.virtualcourse.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép origin cụ thể

public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    // API to disable a course
    @PutMapping("/{courseId}/disable")
    public ResponseEntity<String> disableCourse(@PathVariable Long courseId) {
        courseService.disableCourse(courseId);
        return ResponseEntity.ok("Course disabled successfully");
    }

    // API to enable a course
    @PutMapping("/{courseId}/enable")
    public ResponseEntity<String> enableCourse(@PathVariable Long courseId) {
        courseService.enableCourse(courseId);
        return ResponseEntity.ok("Course enabled successfully");
    }
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        if (course == null) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        return ResponseEntity.ok(course);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student-courses/{accountId}")
    public ResponseEntity<Map<String, List<CourseDTO>>> getStudentCourses(@PathVariable Long accountId) {
        Map<String, List<CourseDTO>> courses = studentService.getStudentCourses(accountId);
        return ResponseEntity.ok(courses);
    }

}
