// src/main/java/com/mytech/virtualcourse/controllers/InstructorController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Category;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.CategoryRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    /**
     * Lấy danh sách tất cả các Instructor.
     */
    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        List<InstructorDTO> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    /**
     * Lấy thông tin Instructor theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * Cập nhật thông tin Instructor.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long id, @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO updatedInstructor = instructorService.updateInstructor(id, instructorDTO);
        return ResponseEntity.ok(updatedInstructor);
    }

    /**
     * Xóa Instructor theo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vô hiệu hóa Instructor.
     */
//    @PutMapping("/{instructorId}/disable")
//    public ResponseEntity<String> disableInstructor(@PathVariable Long instructorId) {
//        instructorService.disableInstructor(instructorId);
//        return ResponseEntity.ok("Instructor account disabled successfully");
//    }
//
//    /**
//     * Kích hoạt Instructor.
//     */
//    @PutMapping("/{instructorId}/enable")
//    public ResponseEntity<String> enableInstructor(@PathVariable Long instructorId) {
//        instructorService.enableInstructor(instructorId);
//        return ResponseEntity.ok("Instructor account enabled successfully");
//    }

    /**
     * Lấy danh sách các khóa học của Instructor.
     */
    @GetMapping("/{instructorId}/courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(@PathVariable("instructorId") Long instructorId) {
        System.out.println("GET /api/instructors/" + instructorId + "/courses called"); // Log kiểm tra
        List<CourseDTO> courses = instructorService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Tạo mới một khóa học cho Instructor.
     */
    @PostMapping("/{instructorId}/courses")
    public ResponseEntity<CourseDTO> createCourseForInstructor(
            @PathVariable Long instructorId,
            @RequestBody CourseDTO courseDTO
    ) {
        // 1) Tìm Instructor
        Instructor instructor = instructorService.findInstructorById(instructorId);

        // 2) Tìm Category
        Category cat = categoryRepository.findById(courseDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 3) Map DTO => Entity
        Course course = courseMapper.courseDTOToCourse(courseDTO);
        course.setInstructor(instructor);
        course.setCategory(cat);
        // ban đầu => PENDING_APPROVAL
        course.setStatus("PENDING_APPROVAL");

        // 4) Lưu
        course = courseRepository.save(course);

        // 5) Map => DTO => return
        CourseDTO savedDTO = courseMapper.courseToCourseDTO(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
    }
}
