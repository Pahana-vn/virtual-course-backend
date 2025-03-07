package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Cart;
import com.mytech.virtualcourse.entities.CartItem;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.CartItemRepository;
import com.mytech.virtualcourse.repositories.CartRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import com.mytech.virtualcourse.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {"http://localhost:3000", "http://10.0.2.2:8080"}, allowCredentials = "true")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseMapper courseMapper;

//    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_INSTRUCTOR')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

//    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_INSTRUCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_INSTRUCTOR')")
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<StudentDTO> getStudentByAccountId(@PathVariable Long accountId) {
        StudentDTO student = studentService.getStudentByAccountId(accountId);
        return ResponseEntity.ok(student);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<DashboardDTO> getStudentDashboard(@PathVariable Long studentId) {
        DashboardDTO dashboard = studentService.getStudentDashboardData(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    //wishlist
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/{studentId}/wishlist")
    public ResponseEntity<String> addToWishlist(@PathVariable Long studentId, @RequestBody CourseDTO courseDTO) {
        studentService.addToWishlist(studentId, courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added to wishlist successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{studentId}/wishlist")
    public ResponseEntity<List<CourseDTO>> getWishlist(
            @PathVariable Long studentId,
            @RequestParam(required = false) String platform) {

        List<CourseDTO> wishlist = studentService.getWishlist(studentId, platform);
        return ResponseEntity.ok(wishlist);
    }


    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @DeleteMapping("/{studentId}/wishlist/{courseId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.removeFromWishlist(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    //cart
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/{studentId}/cart")
    public ResponseEntity<String> addToCart(@PathVariable Long studentId, @Valid @RequestBody CourseDTO courseDTO) {
        System.out.println("Received request to add course ID: " + courseDTO.getId() + " to cart for student ID: " + studentId);
        studentService.addToCart(studentId, courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added to cart successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{studentId}/cart-items")
    public ResponseEntity<?> getCartItems(@PathVariable Long studentId) {
        if (studentId == null || studentId <= 0) {
            return ResponseEntity.badRequest().body("Invalid studentId");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Cart cart = student.getCart();
        if (cart == null) {
            System.out.println("🛠 Tạo giỏ hàng mới cho studentId: " + studentId);
            cart = new Cart();
            cart.setStudent(student);
            cartRepository.save(cart);
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        return ResponseEntity.ok(cartItems.stream()
                .map(cartItem -> new CartItemDTO(cartItem.getId(), courseMapper.courseToCourseDTO(cartItem.getCourse()), cartItem.getQuantity()))
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @DeleteMapping("/{studentId}/cart-items/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long studentId, @PathVariable Long cartItemId) {
        studentService.removeFromCart(studentId, cartItemId);
        return ResponseEntity.status(HttpStatus.OK).body("Course removed from cart successfully");
    }
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/student-courses-status/{studentId}")
    public ResponseEntity<Map<String, List<CourseDTO>>> getStudentCoursesWithProgress(
            @PathVariable Long studentId,
            @RequestParam(required = false) String platform) {

        Map<String, List<CourseDTO>> courses = studentService.getStudentCourses(studentId, platform);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{studentId}/quiz-results")
    public List<StudentQuizResultDTO> getStudentQuizResults(@PathVariable Long studentId) {
        return studentService.getStudentQuizResults(studentId);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/quiz-details/{quizId}")
    public StudentQuizDetailDTO getQuizDetails(@PathVariable Long quizId) {
        return studentService.getQuizDetails(quizId);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PutMapping("/{studentId}/change-password-student")
    public ResponseEntity<?> changePasswordStudent(
            @PathVariable Long studentId,
            @RequestBody ChangePasswordStudentDTO changePasswordDTO) {
        try {
            studentService.changePassword(studentId, changePasswordDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

//    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
//    @GetMapping("/student-courses/{studentId}")
//    public ResponseEntity<Map<String, List<CourseDTO>>> getStudentPurchasedCourses(@PathVariable Long studentId) {
//        if (studentId == null || studentId <= 0) {
//            return ResponseEntity.badRequest().body(Collections.emptyMap());
//        }
//        System.out.println("Received request for student ID: " + studentId);
//        Map<String, List<CourseDTO>> courses = studentService.getStudentPurchasedCourses(studentId);
//        return ResponseEntity.ok(courses);
//    }


}
