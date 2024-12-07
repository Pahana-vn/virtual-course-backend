package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.DashboardDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }


    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<StudentDTO> getStudentByAccountId(@PathVariable Long accountId) {
        StudentDTO student = studentService.getStudentByAccountId(accountId);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/dashboard/{accountId}")
    public ResponseEntity<DashboardDTO> getStudentDashboard(@PathVariable Long accountId) {
        DashboardDTO dashboard = studentService.getStudentDashboardData(accountId);
        return ResponseEntity.ok(dashboard);
    }



    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studentId}/wishlist")
    public ResponseEntity<String> addCourseToWishlist(
            @PathVariable Long studentId,
            @RequestBody CourseDTO courseDTO) {
        try {
            // Thêm khóa học vào wishlist của học viên
            studentService.addCourseToWishlist(studentId, courseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Course added to wishlist successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student or Course not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add course to wishlist: " + e.getMessage());
        }
    }

}
