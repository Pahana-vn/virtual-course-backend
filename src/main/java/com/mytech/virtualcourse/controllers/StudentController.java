package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.DashboardDTO;
import com.mytech.virtualcourse.dtos.StudentDTO;
import com.mytech.virtualcourse.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép origin cụ thể

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



    @PostMapping("/add-student/{accountId}")
    public ResponseEntity<StudentDTO> addStudent(@PathVariable Long accountId, @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(accountId, studentDTO);
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
}
