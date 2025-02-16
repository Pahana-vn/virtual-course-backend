package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SecurityUtils {
    private final InstructorRepository instructorRepository;

    private final StudentRepository studentRepository;

    public SecurityUtils(InstructorRepository instructorRepository, StudentRepository studentRepository) {
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
    }

    public Long getLoggedInAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated user found");
        }

        // Principal được lưu là accountId
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Principal is not an accountId");
    }

    public Long getLoggedInInstructorId() {
        Long accountId = getLoggedInAccountId();
        return instructorRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found for account ID: " + accountId))
                .getId();
    }

    public Long getLoggedInStudentId() {
        Long accountId = getLoggedInAccountId();
        return studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found for account ID: " + accountId))
                .getId();
    }
}
