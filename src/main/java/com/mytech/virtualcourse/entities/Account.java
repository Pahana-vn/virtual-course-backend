package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.AuthenticationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enable = true;

    @Column(name = "verified_email", nullable = false)
    private Boolean verifiedEmail = false;

    private String token;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "authentication_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType; // Kiểu xác thực: local, google, facebook, etc.

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ManyToMany
    @JoinTable(
            name = "account_role_mapping",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Instructor instructor;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Student student;


}
