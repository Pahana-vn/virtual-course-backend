// src/main/java/com/mytech/virtualcourse/services/EnrollmentService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.EnrollmentDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Enrollment;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.EnrollmentMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.EnrollmentRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    /**
     * Enroll a student in a course.
     *
     * @param dto the enrollment data transfer object
     * @return the created EnrollmentDTO
     */
    public EnrollmentDTO enrollStudent(EnrollmentDTO dto) {
        // Check if student exists
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id=" + dto.getStudentId()));

        // Check if course exists
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id=" + dto.getCourseId()));

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())) {
            throw new RuntimeException("Student is already enrolled in this course.");
        }

        // Create Enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setCompleted(false); // Default to not completed

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(savedEnrollment);
    }

    /**
     * Mark an enrollment as completed.
     *
     * @param enrollmentId the ID of the enrollment
     * @return the updated EnrollmentDTO
     */
    public EnrollmentDTO completeEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id=" + enrollmentId));

        enrollment.setCompleted(true);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(updatedEnrollment);
    }

    /**
     * Get all enrollments for a student.
     *
     * @param studentId the ID of the student
     * @return list of EnrollmentDTOs
     */
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findAll()
                .stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .toList();

        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all enrollments for a course.
     *
     * @param courseId the ID of the course
     * @return list of EnrollmentDTOs
     */
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findAll()
                .stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .toList();

        return enrollments.stream()
                .map(enrollmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete an enrollment by ID.
     *
     * @param enrollmentId the ID of the enrollment to delete
     */
    public void deleteEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new ResourceNotFoundException("Enrollment not found with id=" + enrollmentId);
        }
        enrollmentRepository.deleteById(enrollmentId);
    }
}
