// src/main/java/com/mytech/virtualcourse/entities/Instructor.java

package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "instructor")
public class Instructor extends AbstractEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "status", nullable = false)
    private String status; // This will store "active" or "inactive"

    private String address;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "verified_phone", nullable = false)
    private boolean verifiedPhone = false; // Changed to primitive type

    private String photo;

    private String title;

    private String workplace;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;

    @OneToOne(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wallet wallet;

    @OneToOne(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankAccount bankAccount;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests;
}
