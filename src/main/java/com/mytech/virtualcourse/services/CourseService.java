package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Lecture;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.LearningProgressRepository;
import com.mytech.virtualcourse.repositories.StudentLectureProgressRepository;
import com.mytech.virtualcourse.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

                        // Thêm URL đầy đủ cho instructor photo
                        if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                            dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.getSections().forEach(section -> {
            section.getLectures().forEach(lecture -> {
                lecture.getArticles().size();
            });
        });

        CourseDTO dto = courseMapper.courseToCourseDTO(course);
        if (course.getImageCover() != null) {
            dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
            if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
            }
        }
        return dto;
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

        // Lấy tất cả bài giảng trong khóa học
        List<Lecture> allLectures = course.getSections().stream()
                .flatMap(section -> section.getLectures().stream())
                .collect(Collectors.toList());
        int totalLectures = allLectures.size();

        // Lấy danh sách các ID bài giảng đã hoàn thành
        List<Long> completedLectureIds = studentLectureProgressRepository.findCompletedLectureIdsByStudentAndCourse(studentId, courseId);

        // Map sections và bài giảng, đặt trạng thái 'completed'
        List<SectionDTO> sectionsDTO = course.getSections().stream().map(section -> {
            SectionDTO sectionDTO = new SectionDTO();
            sectionDTO.setId(section.getId());
            sectionDTO.setTitleSection(section.getTitleSection());
            sectionDTO.setLectures(section.getLectures().stream().map(lecture -> {
                LectureDTO lectureDTO = new LectureDTO();
                lectureDTO.setId(lecture.getId());
                lectureDTO.setTitleLecture(lecture.getTitleLecture());
                lectureDTO.setLectureVideo(lecture.getLectureVideo());
                lectureDTO.setLectureResource(lecture.getLectureResource());
                lectureDTO.setLectureOrder(lecture.getLectureOrder());
                lectureDTO.setArticles(lecture.getArticles().stream()
                        .map(article -> new ArticleDTO(article.getId(), article.getContent(), article.getFileUrl()))
                        .collect(Collectors.toList()));
                lectureDTO.setCompleted(completedLectureIds.contains(lecture.getId()));
                return lectureDTO;
            }).collect(Collectors.toList()));
            return sectionDTO;
        }).collect(Collectors.toList());

        dto.setSections(sectionsDTO);

        // Kiểm tra xem tất cả bài giảng đã hoàn thành chưa
        boolean allCompleted = completedLectureIds.size() == totalLectures && totalLectures > 0;
        dto.setAllLecturesCompleted(allCompleted);

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
