package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.CourseDetailDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Lecture;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.CourseLevel;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.LearningProgressRepository;
import com.mytech.virtualcourse.repositories.StudentLectureProgressRepository;
import com.mytech.virtualcourse.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private StudentLectureProgressRepository studentLectureProgressRepository;


    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {

                        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        CourseDTO dto = courseMapper.courseToCourseDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        }
        return dto;
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByTitleCourse(courseDTO.getTitleCourse())) {
            throw new IllegalArgumentException("Course with title '" + courseDTO.getTitleCourse() + "' already exists");
        }
        Course course = courseMapper.courseDTOToCourse(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.courseToCourseDTO(savedCourse);
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        existingCourse.setTitleCourse(courseDTO.getTitleCourse());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setBasePrice(courseDTO.getBasePrice());
        existingCourse.setDuration(courseDTO.getDuration());
        existingCourse.setImageCover(courseDTO.getImageCover());
        existingCourse.setUrlVideo(courseDTO.getUrlVideo());
        existingCourse.setStatus(courseDTO.getStatus());
        existingCourse.setHashtag(courseDTO.getHashtag());


        if (courseDTO.getLevel() != null) {
            existingCourse.setLevel(CourseLevel.valueOf(courseDTO.getLevel().toUpperCase()));
        }

        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.courseToCourseDTO(updatedCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public List<CourseDTO> mapCoursesWithFullImageUrl(List<Course> courses) {
        return courses.stream()
                .map(course -> {
                    CourseDTO dto = courseMapper.courseToCourseDTO(course);
                    if (course.getImageCover() != null) {
                        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CourseDetailDTO getCourseDetailsById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        CourseDetailDTO dto = courseMapper.courseToCourseDetailDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        }
        return dto;
    }

    public CourseDetailDTO getCourseDetailsForStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        CourseDetailDTO dto = courseMapper.courseToCourseDetailDTO(course);

        // Lấy tất cả lecture trong khóa
        List<Lecture> allLectures = new ArrayList<>();
        course.getSections().forEach(sec -> allLectures.addAll(sec.getLectures()));
        int totalLectures = allLectures.size();

        // Đếm lecture đã hoàn thành bằng StudentLectureProgress
        int completedCount = studentLectureProgressRepository.countCompletedLecturesByStudentAndCourse(studentId, courseId);
        dto.setAllLecturesCompleted(completedCount == totalLectures && totalLectures > 0);

        // Kiểm tra test cuối khóa
        Optional<Test> finalTestOpt = testRepository.findFinalTestByCourseId(courseId);
        if (finalTestOpt.isPresent()) {
            dto.setFinalTestId(finalTestOpt.get().getId());
            dto.setFinalTestTitle(finalTestOpt.get().getTitle());
        }

        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        }
        return dto;
    }
}
