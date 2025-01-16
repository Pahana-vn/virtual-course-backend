package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.entities.FavoriteCourse;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.FavoriteCourseRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteCourseService {

    @Autowired
    private FavoriteCourseRepository favoriteCourseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseMapper courseMapper;

    public List<CourseDTO> getFavoriteByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        List<FavoriteCourse> favoriteCourses = favoriteCourseRepository.findByStudent(student);
        return favoriteCourses.stream()
                .map(favoriteCourse -> courseMapper.courseToCourseDTO(favoriteCourse.getCourse()))
                .collect(Collectors.toList());
    }
}