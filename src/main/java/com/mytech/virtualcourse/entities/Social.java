package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Social extends AbstractEntity{

    @OneToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @Column(nullable = false)
    private String facebookUrl;

    @Column(nullable = false)
    private String googleUrl;

    @Column(nullable = false)
    private String instagramUrl;

    @Column(nullable = false)
    private String linkedinUrl;
}
