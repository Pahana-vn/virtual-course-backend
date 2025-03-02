package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/complete-lecture")
    public void completeLecture(@RequestParam Long studentId, @RequestParam Long lectureId) {
        progressService.markLectureAsCompleted(studentId, lectureId);
    }
    @PostMapping("/student/{studentId}/lecture/{lectureId}/complete")
    public ResponseEntity<Void> markLectureAsCompleted(@PathVariable Long studentId, @PathVariable Long lectureId) {
        progressService.markLectureAsCompleted(studentId, lectureId);
        return ResponseEntity.noContent().build();
    }
}
