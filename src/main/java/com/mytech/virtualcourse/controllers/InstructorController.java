// src/main/java/com/mytech/virtualcourse/controllers/InstructorController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.services.InstructorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép origin cụ thể
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

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
     * Tạo mới Instructor.
     */
//    @PostMapping
//    public ResponseEntity<InstructorDTO> createInstructor(@Valid @RequestBody InstructorDTO instructorDTO) {
//        InstructorDTO createdInstructor = instructorService.createInstructor(instructorDTO);
//        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
//    }
    /**
     * Thêm Instructor cho Account cụ thể.
     */
    @PostMapping("/add-instructor/{accountId}")
    public ResponseEntity<InstructorDTO> addInstructor(
            @PathVariable Long accountId,
            @RequestBody InstructorDTO instructorDTO) {
        InstructorDTO createdInstructor = instructorService.addInstructorToAccount(accountId, instructorDTO);
        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
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
    @PutMapping("/{instructorId}/disable")
    public ResponseEntity<String> disableInstructor(@PathVariable Long instructorId) {
        instructorService.disableInstructor(instructorId);
        return ResponseEntity.ok("Instructor account disabled successfully");
    }

    /**
     * Kích hoạt Instructor.
     */
    @PutMapping("/{instructorId}/enable")
    public ResponseEntity<String> enableInstructor(@PathVariable Long instructorId) {
        instructorService.enableInstructor(instructorId);
        return ResponseEntity.ok("Instructor account enabled successfully");
    }
}
