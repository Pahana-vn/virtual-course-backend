package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.CourseDetailDTO;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.mytech.virtualcourse.services.CourseService;
import com.mytech.virtualcourse.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

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
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
//        System.out.println("Received CourseDTO for update: " + courseDTO);
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{instructorId}/instructor-courses")
    public ResponseEntity<List<CourseDTO>> getInstructorCourses(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String status) {
        List<CourseDTO> courseDTOs;
        if (status != null) {
            courseDTOs = courseService.getCoursesByInstructorIdIdAndStatus(instructorId, ECourseStatus.valueOf(status));
        } else {
            courseDTOs = courseService.getCoursesByInstructorId(instructorId);
        }
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/student-courses/{accountId}")
    public ResponseEntity<Map<String, List<CourseDTO>>> getStudentCourses(@PathVariable Long accountId) {
        Map<String, List<CourseDTO>> courses = studentService.getStudentCourses(accountId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}/course-details")
    public ResponseEntity<CourseDetailDTO> getCourseDetailsById(@PathVariable Long id) {
        CourseDetailDTO courseDetails = courseService.getCourseDetailsById(id);
        return ResponseEntity.ok(courseDetails);
    }

    @GetMapping("/{courseId}/details-for-student")
    public ResponseEntity<CourseDetailDTO> getCourseDetailsForStudent(
            @PathVariable Long courseId,
            @RequestParam Long studentId) {
        CourseDetailDTO courseDetails = courseService.getCourseDetailsForStudent(courseId, studentId);
        return ResponseEntity.ok(courseDetails);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseDTO>> getCoursesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "10") int limit) {

        List<CourseDTO> courses = courseService.getCoursesByCategory(categoryId, limit);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<CourseDTO>> getCourses(
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(required = false) List<Long> instructorId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<CourseDTO> courses = courseService.getFilteredCourses(categoryId, instructorId, minPrice, maxPrice, search, pageable);
        return ResponseEntity.ok(courses);
    }
}
