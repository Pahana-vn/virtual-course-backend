package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EAccountStatus status = EAccountStatus.ACTIVE;

    @Column(name = "verified_email", nullable = false)
    private Boolean verifiedEmail = false;

    private String token;

    private LocalDateTime tokenExpiry;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    @Version
    private Integer version = 0;

    @Column(name = "authentication_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role_mapping",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Instructor instructor;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Student student;

}
