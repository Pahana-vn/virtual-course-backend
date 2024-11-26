package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.InstructorDTO;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.InstructorMapper;
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
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorMapper instructorMapper;

    public List<InstructorDTO> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream()
                .map(instructor -> {
                    InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
                    // Cập nhật đường dẫn ảnh
                    if (instructor.getPhoto() != null) {
                        dto.setPhoto("/uploads/instructor/" + instructor.getPhoto());
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
            dto.setPhoto("/uploads/instructor/" + instructor.getPhoto());
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
}
