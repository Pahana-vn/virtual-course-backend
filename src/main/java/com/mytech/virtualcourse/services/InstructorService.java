package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.dtos.InstructorStatisticsDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorMapper instructorMapper;

    @Autowired
    private CourseMapper courseMapper;

    public List<InstructorDTO> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream()
                .map(instructor -> {
                    InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
                    // Cập nhật đường dẫn ảnh
                    if (instructor.getPhoto() != null) {
                        dto.setPhoto("http://localhost:8080/uploads/instructor/" + instructor.getPhoto());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public InstructorDTO getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
        // Cập nhật đường dẫn ảnh
        if (instructor.getPhoto() != null) {
            dto.setPhoto("http://localhost:8080/uploads/instructor/" + instructor.getPhoto());
        }
        return dto;
    }

    public InstructorDTO createInstructor(InstructorDTO instructorDTO) {
        Instructor instructor = instructorMapper.instructorDTOToInstructor(instructorDTO);

        if (instructorDTO.getGender() != null) {
            instructor.setGender(Gender.valueOf(instructorDTO.getGender().toUpperCase()));
        }
        Instructor savedInstructor = instructorRepository.save(instructor);
        return instructorMapper.instructorToInstructorDTO(savedInstructor);
    }

    public InstructorDTO updateInstructor(Long id, InstructorDTO instructorDTO) {
        Instructor existingInstructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));

        existingInstructor.setFirstName(instructorDTO.getFirstName());
        existingInstructor.setLastName(instructorDTO.getLastName());
        existingInstructor.setAddress(instructorDTO.getAddress());
        existingInstructor.setPhone(instructorDTO.getPhone());
        existingInstructor.setBio(instructorDTO.getBio());
        existingInstructor.setTitle(instructorDTO.getTitle());
        existingInstructor.setPhoto(instructorDTO.getPhoto());
        existingInstructor.setWorkplace(instructorDTO.getWorkplace());
        existingInstructor.setVerifiedPhone(instructorDTO.getVerifiedPhone());


        if (instructorDTO.getGender() != null) {
            existingInstructor.setGender(Gender.valueOf(instructorDTO.getGender().toUpperCase()));
        }

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return instructorMapper.instructorToInstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + id);
        }
        instructorRepository.deleteById(id);
    }

    // Phương thức lấy các khóa học mà Instructor tạo
    public List<CourseDTO> getCoursesByInstructor(Long instructorId) {
        // Lấy Instructor theo id
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        // Lấy danh sách khóa học của Instructor
        List<Course> courses = courseRepository.findByInstructor(instructor);
        return(courses.stream().map(course -> {
            // Chuyển đổi từ List<Course> sang List<CourseDTO>
            CourseDTO dto = courseMapper.courseToCourseDTO(course);
            if (course.getImageCover() != null) {
                dto.setImageCover("http://localhost:8080/uploads/courses/" + course.getImageCover());
                if (course.getInstructor() != null && course.getInstructor().getPhoto() != null) {
                    dto.getInstructorInfo().setPhoto("http://localhost:8080/uploads/instructor/" + course.getInstructor().getPhoto());
                }
            }
            return dto;
        })).collect(Collectors.toList());
    }

    public InstructorStatisticsDTO getInstructorStatistics(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Long totalCourses = instructorRepository.countCoursesByInstructorId(instructorId);
        Long totalStudents = instructorRepository.countStudentsInInstructorCourses(instructorId);
        BigDecimal balance = instructor.getWallet() != null
                ? instructor.getWallet().getBalance()
                : BigDecimal.ZERO;

        return instructorMapper.toInstructorStatisticsDTO(instructor, totalCourses, totalStudents, balance);
    }
    public String getInstructorAvatar(Long id) {

        if (!instructorRepository.existsInstructorByAccountId(id)) {
            throw new ResourceNotFoundException("Instructor not found with account id: " + id);
        }

        Instructor instructor = instructorRepository.findByAccountId(id);

        return instructor.getPhoto();
    }
}
