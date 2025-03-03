package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.services.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByCourseId(@PathVariable Long courseId) {
        List<SectionDTO> sections = sectionService.getSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
    }
}
