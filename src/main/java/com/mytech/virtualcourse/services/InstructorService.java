// src/main/java/com/mytech/virtualcourse/services/InstructorService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.enums.RoleName;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstructorService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AccountRepository accountRepository; // Thêm repository của Account

    @Autowired
    private InstructorMapper instructorMapper;

    private Instructor findInstructorById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    public void disableInstructor(Long instructorId) {
        Instructor instructor = findInstructorById(instructorId);
        instructor.setStatus("inactive");
        instructorRepository.save(instructor);
    }

    public void enableInstructor(Long instructorId) {
        Instructor instructor = findInstructorById(instructorId);
        instructor.setStatus("active");
        instructorRepository.save(instructor);
    }

    public List<InstructorDTO> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream()
                .map(instructorMapper::instructorToInstructorDTO)
                .collect(Collectors.toList());
    }

    public InstructorDTO getInstructorById(Long id) {
        Instructor instructor = findInstructorById(id);
        return instructorMapper.instructorToInstructorDTO(instructor);
    }

    /**
     * Cập nhật thông tin Instructor.
     */
    public InstructorDTO updateInstructor(Long id, InstructorDTO instructorDTO) {
        Instructor existingInstructor = findInstructorById(id);
// Nếu photo cũ khác photo mới => xóa photo cũ
        if (existingInstructor.getPhoto() != null
                && !existingInstructor.getPhoto().isEmpty()
                && instructorDTO.getPhoto() != null
                && !instructorDTO.getPhoto().equals(existingInstructor.getPhoto())) {

            // Xóa file cũ trong uploads/instructor
            fileStorageService.deleteFile(existingInstructor.getPhoto(), "instructor");
        }
        // Cập nhật các trường
        existingInstructor.setFirstName(instructorDTO.getFirstName());
        existingInstructor.setLastName(instructorDTO.getLastName());
        existingInstructor.setGender(Gender.valueOf(String.valueOf(instructorDTO.getGender())));
        existingInstructor.setAddress(instructorDTO.getAddress());
        existingInstructor.setPhone(instructorDTO.getPhone());
        existingInstructor.setBio(instructorDTO.getBio());
        existingInstructor.setPhoto(instructorDTO.getPhoto());
        existingInstructor.setTitle(instructorDTO.getTitle());
        existingInstructor.setWorkplace(instructorDTO.getWorkplace());
        existingInstructor.setStatus(instructorDTO.getStatus());

        // Cập nhật photo (nếu có)
        if (instructorDTO.getPhoto() != null) {
            existingInstructor.setPhoto(instructorDTO.getPhoto());
        }

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return instructorMapper.instructorToInstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Long id) {
        Instructor instructor = findInstructorById(id);
        instructorRepository.delete(instructor);
    }

    public InstructorDTO createInstructor(InstructorDTO instructorDTO) {
        // Lấy Account từ accountId
        Account account = accountRepository.findById(instructorDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + instructorDTO.getAccountId()));

        // Kiểm tra xem Account đã có Instructor chưa
        if (account.getInstructor() != null) {
            throw new RuntimeException("Account already has an Instructor.");
        }

        // Map DTO sang Entity
        Instructor instructor = instructorMapper.instructorDTOToInstructor(instructorDTO);
        instructor.setAccount(account); // Thiết lập Account cho Instructor

        // Liên kết Instructor với Account
        account.setInstructor(instructor);

        // Lưu Instructor vào cơ sở dữ liệu
        Instructor savedInstructor = instructorRepository.save(instructor);

        // Map lại Entity sang DTO
        return instructorMapper.instructorToInstructorDTO(savedInstructor);
    }

    public InstructorDTO addInstructorToAccount(Long accountId, InstructorDTO instructorDTO) {
        // Lấy Account từ accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Kiểm tra xem Account đã có Instructor chưa
        if (account.getInstructor() != null) {
            throw new RuntimeException("Account already has an Instructor.");
        }

        // Kiểm tra xem Account có ROLE_INSTRUCTOR không
        boolean hasInstructorRole = account.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.INSTRUCTOR);
        if (!hasInstructorRole) {
            throw new RuntimeException("Account does not have the ROLE_INSTRUCTOR role.");
        }

        // Tạo Instructor mới từ DTO
        Instructor instructor = instructorMapper.instructorDTOToInstructor(instructorDTO);
        instructor.setGender(instructorDTO.getGender()); // Đảm bảo Gender được set đúng

        instructor.setAccount(account); // Thiết lập Account cho Instructor

        // Liên kết Instructor với Account
        account.setInstructor(instructor);

        // Lưu Instructor vào cơ sở dữ liệu
        Instructor savedInstructor = instructorRepository.save(instructor);

        // Map lại Entity sang DTO
        return instructorMapper.instructorToInstructorDTO(savedInstructor);
    }
}
