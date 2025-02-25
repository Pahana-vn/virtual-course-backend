package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.repositories.*;
import com.mytech.virtualcourse.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
    private SectionRepository sectionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InstructorMapper instructorMapper;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<InstructorDTO> getAllInstructors(String platform) {
        List<Instructor> instructors = instructorRepository.findAll();

        // Nếu có tham số platform=flutter, dùng 10.0.2.2 (Android Emulator)
        String baseUrl = (platform != null && platform.equals("flutter"))
                ? "http://10.0.2.2:8080"
                : "http://localhost:8080";

        return instructors.stream()
                .map(instructor -> {
                    InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
                    // Cập nhật đường dẫn ảnh
                    if (instructor.getPhoto() != null) {
                        dto.setPhoto(baseUrl + "/uploads/instructor/" + instructor.getPhoto());
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

    public InstructorDetailsDTO getInstructorDetails(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        int totalCourses = courseRepository.countByInstructorId(instructorId);
        int totalSections = sectionRepository.countByInstructorId(instructorId);
        int totalStudents = paymentRepository.countDistinctStudentsByInstructorId(instructorId);
        double averageRating = instructorRepository.calculateAverageRatingByInstructorId(instructorId);

        return InstructorMapper.MAPPER.instructorToInstructorDetailsDTO(
                instructor, totalCourses, totalSections, totalStudents, averageRating
        );
    }

    public InstructorProfileDTO getProfileByInstructorId(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));

        return instructorMapper.instructorToInstructorProfileDTO(instructor);
    }

    public InstructorProfileDTO updateProfileByInstructorId(Long id, InstructorProfileDTO profileDTO) {

        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

        instructor.setFirstName(profileDTO.getFirstName());
        instructor.setLastName(profileDTO.getLastName());
        instructor.setGender(Gender.valueOf(profileDTO.getGender().toUpperCase()));
        instructor.setAddress(profileDTO.getAddress());
        instructor.setPhone(profileDTO.getPhone());
        instructor.setBio(profileDTO.getBio());
        instructor.setTitle(profileDTO.getTitle());
        instructor.setWorkplace(profileDTO.getWorkplace());

        if (profileDTO.getPhoto() != null) {
            instructor.setPhoto(profileDTO.getPhoto());
        }

        instructor = instructorRepository.save(instructor);

        // Map the updated instructor entity to the InstructorProfileDTO
        return InstructorMapper.MAPPER.instructorToInstructorProfileDTO(instructor);
    }

    public InstructorStatisticsDTO getInstructorStatistics(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        int totalCourses = instructorRepository.countCoursesByInstructorId(id);
        int totalPublishedCourses = instructorRepository.countPublishedCoursesByInstructorId(id);
        int totalPendingCourses = instructorRepository.countPendingCoursesByInstructorId(id);
        int totalStudents = instructorRepository.countStudentsInInstructorCourses(id);
        int totalPurchasedCourses = studentRepository.countPurchasedCoursesByInstructorId(id);
        int totalTransactions = transactionRepository.countTransactionsByInstructorId(id);
        int totalDeposits = transactionRepository.countDepositsInTransactionsByInstructorId(id);
        int totalWithdrawals = transactionRepository.countWithdrawalsInTransactionsByInstructorId(id);

        BigDecimal balance = instructor.getWallet() != null
                ? instructor.getWallet().getBalance()
                : BigDecimal.ZERO;

        return instructorMapper.toInstructorStatisticsDTO(instructor, totalCourses, totalPublishedCourses, totalPendingCourses, totalStudents,totalPurchasedCourses,totalTransactions,totalDeposits,totalWithdrawals, balance);
    }

    public InstructorProfileDTO getProfileByLoggedInInstructor(HttpServletRequest request) {
        Long instructorId = getInstructorIdFromRequest(request);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with Instructor ID: " + instructorId));

        return instructorMapper.instructorToInstructorProfileDTO(instructor);
    }


    public InstructorProfileDTO updateProfileByLoggedInInstructor(HttpServletRequest request, InstructorProfileDTO profileDTO) {
        Long instructorId = getInstructorIdFromRequest(request);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with instructor ID: " + instructorId));

        instructor.setFirstName(profileDTO.getFirstName());
        instructor.setLastName(profileDTO.getLastName());
        instructor.setGender(profileDTO.getGender() != null
                ? Gender.valueOf(profileDTO.getGender().toUpperCase())
                : instructor.getGender());
        instructor.setAddress(profileDTO.getAddress());
        instructor.setPhone(profileDTO.getPhone());
        instructor.setBio(profileDTO.getBio());
        instructor.setTitle(profileDTO.getTitle());
        instructor.setWorkplace(profileDTO.getWorkplace());
        instructor.setPhoto(profileDTO.getPhoto());
        instructor.setVerifiedPhone(profileDTO.getVerifiedPhone());

        Instructor updatedInstructor = instructorRepository.save(instructor);

        return instructorMapper.instructorToInstructorProfileDTO(updatedInstructor);
    }

    private Long getInstructorIdFromRequest(HttpServletRequest request) {
        String jwt = getJwtFromCookies(request);
        return jwtUtil.getInstructorIdFromJwtToken(jwt);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        return jwtUtil.getCookieValueByName(request, "token");
    }

}
