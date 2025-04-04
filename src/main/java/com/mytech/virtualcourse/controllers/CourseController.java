package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.CourseDetailDTO;
import com.mytech.virtualcourse.dtos.CourseStatusDTO;
import com.mytech.virtualcourse.entities.Course;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(
            @RequestParam(required = false) String platform) {
        List<CourseDTO> courses = courseService.getAllCourses(platform);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/status")
    public ResponseEntity<List<CourseDTO>> getAllCoursesByStatus(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false, defaultValue = "PUBLISHED") String status) {

        List<CourseDTO> courses = courseService.getAllCoursesByStatus(platform, status);
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

    @GetMapping("/instructor/{instructorId}/purchased")
    public ResponseEntity<List<CourseDTO>> getPurchasedCourses(@PathVariable Long instructorId) {
        List<CourseDTO> courses = courseService.getPurchasedCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }


    @GetMapping("/{id}/course-details")
    public ResponseEntity<CourseDetailDTO> getCourseDetailsById(@PathVariable Long id) {
        CourseDetailDTO courseDetails = courseService.getCourseDetailsById(id);
        return ResponseEntity.ok(courseDetails);
    }

    @PutMapping("/{courseId}/update-status")
    public ResponseEntity<?> updateCourseStatus(@PathVariable Long courseId, @RequestBody CourseStatusDTO request) {
        try {
            Course updatedCourse = courseService.updateCourseStatus(courseId, request.getStatus());
            return ResponseEntity.ok(Map.of("message", "Course status updated successfully", "status", updatedCourse.getStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update course status"));
        }
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{courseId}/details-for-student")
    public ResponseEntity<CourseDetailDTO> getCourseDetailsForStudent(
            @PathVariable Long courseId,
            @RequestParam Long studentId,
            @RequestParam(required = false) String platform) {

        CourseDetailDTO courseDetails = courseService.getCourseDetailsForStudent(courseId, studentId, platform);
        return ResponseEntity.ok(courseDetails);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<CourseDTO>> getCourses(
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(required = false) List<Long> instructorId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "PUBLISHED") ECourseStatus status,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<CourseDTO> courses = courseService.getFilteredCourses(categoryId, instructorId, minPrice, maxPrice, search, status, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CourseDTO>> getCoursesByCategoryId(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String platform) {
        System.out.println("Received request for categoryId: " + categoryId + ", platform: " + platform);
        List<CourseDTO> courses = courseService.getCoursesByCategoryId(categoryId, platform);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search-flutter")
    public ResponseEntity<List<CourseDTO>> searchCoursesFlutter(
            @RequestParam String keyword,
            @RequestParam(required = false) String platform) {
        List<CourseDTO> courses = courseService.searchCoursesFlutter(keyword, platform);
        return ResponseEntity.ok(courses);
    }
}
