// src/main/java/com/mytech/virtualcourse/entities/Account.java
package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    private Boolean enable;
    private Boolean verifiedEmail;

    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    private String password;
    private String type;
    private String status;
    private Integer version;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Instructor instructor;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Student student;

    // Getters and Setters (Lombok đã tạo)
// Thêm trường mới
    private String resetPasswordToken;
}
