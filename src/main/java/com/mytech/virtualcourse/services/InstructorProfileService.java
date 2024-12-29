package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.InstructorProfileDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.mappers.ProfileMapper;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InstructorProfileService {
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final InstructorRepository instructorRepository;
    @Autowired
    private final ProfileMapper profileMapper;

    public InstructorProfileService(AccountRepository accountRepository,
                          InstructorRepository instructorRepository,
                          ProfileMapper profileMapper) {
        this.accountRepository = accountRepository;
        this.instructorRepository = instructorRepository;
        this.profileMapper = profileMapper;
    }

    @Transactional(readOnly = true)
    public InstructorProfileDTO getProfileByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        Instructor instructor = instructorRepository.findByAccountId(account.getId());
        InstructorProfileDTO dto = profileMapper.EntitiestoInstructorProfileDTO(account, instructor);
        if (instructor.getPhoto() != null) {
            dto.setPhoto("http://localhost:8080/uploads/instructor/" + instructor.getPhoto());
        }
        return dto;
    }
}
