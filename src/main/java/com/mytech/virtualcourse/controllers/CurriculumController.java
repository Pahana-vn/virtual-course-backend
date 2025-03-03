package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CurriculumImportRequest;
import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.entities.Section;
import com.mytech.virtualcourse.services.CurriculumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curriculums")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importCurriculum(@RequestBody CurriculumImportRequest request) {
        List<Section> sections = curriculumService.importCurriculum(request);
        return ResponseEntity.ok().body(Map.of("message", "Import thành công", "sections", sections));
    }

    @GetMapping("/export/{courseId}")
    public ResponseEntity<?> exportCurriculum(@PathVariable Long courseId) {
        List<SectionDTO> sections = curriculumService.exportCurriculum(courseId);
        return ResponseEntity.ok().body(Map.of("message", "Export thành công", "sections", sections));
    }
}
