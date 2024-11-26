package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.services.FavoriteCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteCourseController {

    @Autowired
    private FavoriteCourseService favoriteCourseService;

    @GetMapping("/{studentId}")
    public ResponseEntity<List<CourseDTO>> getWishlist(@PathVariable Long studentId) {
        List<CourseDTO> wishlistCourses = favoriteCourseService.getFavoriteByStudentId(studentId);
        return ResponseEntity.ok(wishlistCourses);
    }
}
