// src/main/java/com/mytech/virtualcourse/entities/Role.java
package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts;

    // Getters and Setters (Lombok đã tạo)
}
