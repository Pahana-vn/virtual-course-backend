package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.InstructorProfileDTO;
import com.mytech.virtualcourse.services.InstructorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor/profile")
public class InstructorProfileController {

    @Autowired
    private final InstructorProfileService instructorProfileService;

    public InstructorProfileController(InstructorProfileService instructorProfileService) {
        this.instructorProfileService = instructorProfileService;
    }

    @GetMapping()
    public ResponseEntity<InstructorProfileDTO> getInstructorProfile(@RequestParam String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Hoặc trả về một thông báo lỗi
        }
        InstructorProfileDTO instructorProfile = instructorProfileService.getProfileByUsername(username);
        return ResponseEntity.ok(instructorProfile);
    }
}