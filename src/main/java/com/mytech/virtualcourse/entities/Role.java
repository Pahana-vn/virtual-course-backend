package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    public ERole name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private List<Account> accounts;
}