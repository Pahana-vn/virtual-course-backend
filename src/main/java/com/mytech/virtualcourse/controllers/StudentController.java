package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CartItemDTO;
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
import java.util.Map;

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

    @PostMapping("/{studentId}/cart")
    public ResponseEntity<String> addCourseToCart(
            @PathVariable Long studentId,
            @RequestBody CourseDTO courseDTO) {
        try {
            studentService.addCourseToCart(studentId, courseDTO, 1);
            return ResponseEntity.status(HttpStatus.CREATED).body("Course added to cart successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student or Course not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add course to cart: " + e.getMessage());
        }
    }

    @GetMapping("/{studentId}/cart-items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long studentId) {
        List<CartItemDTO> cartItems = studentService.getCartItemsForStudent(studentId);
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/{studentId}/cart-items/{cartItemId}")
    public ResponseEntity<String> removeCourseFromCart(
            @PathVariable Long studentId,
            @PathVariable Long cartItemId) {
        try {
            studentService.removeCourseFromCart(studentId, cartItemId);
            return ResponseEntity.status(HttpStatus.OK).body("Course removed from cart successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove course from cart: " + e.getMessage());
        }
    }

    @GetMapping("/student-courses-status/{studentId}")
    public ResponseEntity<Map<String, List<CourseDTO>>> getStudentCoursesWithProgress(@PathVariable Long studentId) {
        Map<String, List<CourseDTO>> courses = studentService.getStudentCourses(studentId);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{studentId}/wishlist/{courseId}")
    public ResponseEntity<Void> removeCourseFromWishlist(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.removeCourseFromWishlist(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    

}
