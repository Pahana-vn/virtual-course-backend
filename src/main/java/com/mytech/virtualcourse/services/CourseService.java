package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.enums.CourseLevel;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.EnrollmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // Method to disable a course (set status to "inactive")
    public void disableCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus("inactive");
        courseRepository.save(course);
    }

    // Method to enable a course (set status to "active")
    public void enableCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus("active");
        courseRepository.save(course);
    }

    public List<CourseDTO> getAllCourses(Pageable pageable) {
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
        // Gửi thông báo đến tất cả sinh viên đã đăng ký (nếu có)
        List<Student> students = savedCourse.getStudents();
        for (Student student : students) {
            notificationService.sendNotification(
                    student.getId(),
                    "Khóa học '" + savedCourse.getTitleCourse() + "' mới đã được tạo.",
                    NotificationType.COURSE_UPDATE,
                    savedCourse.getId(),
                    null
            );
        }
        return courseMapper.courseToCourseDTO(savedCourse);
    }

    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
// Kiểm tra xem có Enrollment nào không
if (!enrollmentRepository.findByCourseId(id).isEmpty()) {
    throw new IllegalStateException("Cannot edit course with enrolled students.");
}
        // Nếu imageCover cũ khác imageCover mới => xóa imageCover cũ
        if (existingCourse.getImageCover() != null
                && !existingCourse.getImageCover().isEmpty()
                && courseDTO.getImageCover() != null
                && !courseDTO.getImageCover().equals(existingCourse.getImageCover())) {

            // Xóa file cũ trong uploads/course
            fileStorageService.deleteFile(existingCourse.getImageCover(), "course");
        }
        existingCourse.setTitleCourse(courseDTO.getTitleCourse());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setBasePrice(courseDTO.getBasePrice());
        existingCourse.setDuration(courseDTO.getDuration());
        existingCourse.setImageCover(courseDTO.getImageCover());
        existingCourse.setUrlVideo(courseDTO.getUrlVideo());
        existingCourse.setStatus(courseDTO.getStatus());
        existingCourse.setHashtag(courseDTO.getHashtag());

// Cập nhật imageCover (nếu có)
        if (courseDTO.getImageCover() != null) {
            existingCourse.setImageCover(courseDTO.getImageCover());
        }

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

    // Trong CourseService.java

    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        if (!course.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Course is not in a state to be approved.");
        }
        course.setStatus("ACTIVE");
        courseRepository.save(course);

        // Gửi thông báo đến giảng viên về việc khóa học đã được phê duyệt
        notificationService.sendNotification(
                course.getInstructor().getAccount().getId(),
                "Khóa học '" + course.getTitleCourse() + "' của bạn đã được phê duyệt.",
                NotificationType.COURSE_APPROVED,
                course.getId(),
                null
        );
    }

}
