package com.mytech.virtualcourse.security;

import java.security.Principal;
import java.util.List;

public class CustomPrincipal implements Principal {

    private final String username;
    private final Long accountId;
    private final Long studentId;
    private final Long instructorId;
    private final List<String> roles;

    public CustomPrincipal(String username, Long accountId, Long studentId, Long instructorId, List<String> roles) {
        this.username = username;
        this.accountId = accountId;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return username;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public List<String> getRoles() {
        return roles;
    }
}