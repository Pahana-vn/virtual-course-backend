package com.mytech.virtualcourse.entities;

import com.mytech.virtualcourse.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course")
public class Course extends AbstractEntity {

    @Column(name = "title_course", nullable = false)
    private String titleCourse;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level; // Mức độ khóa học (BEGINNER, INTERMEDIATE, ADVANCED)

    private String imageCover;

    private String urlVideo;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    private String hashtag;

    private Integer duration; // Tổng thời lượng khóa học (phút)

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String status; // Trạng thái khóa học (e.g., ACTIVE, INACTIVE)

    @ManyToMany
    @JoinTable(
            name = "student_course_mapping",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students; // Sinh viên đăng ký khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections; // Các phần học trong khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews; // Đánh giá khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteCourse> favoriteCourses; // Danh sách yêu thích

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoursePromotion> promotions; // Chương trình khuyến mãi

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments; // Thanh toán liên quan đến khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments; // Bài tập liên quan đến khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests; // Bài kiểm tra liên quan đến khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningProgress> learningProgresses; // Tiến độ học của sinh viên

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications; // Chứng chỉ cấp cho sinh viên

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningSchedule> learningSchedules;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudySession> studySessions;

}
