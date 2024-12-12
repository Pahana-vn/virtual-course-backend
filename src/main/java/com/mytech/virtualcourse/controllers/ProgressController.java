package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
