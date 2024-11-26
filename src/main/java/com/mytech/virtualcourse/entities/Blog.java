package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blog")
public class Blog extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title; // Tiêu đề bài viết

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription; // Mô tả ngắn

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung bài viết

    @Column(name = "image_main")
    private String imageMain; // Ảnh đại diện

    @Column(name = "image_background")
    private String imageBackground; // Ảnh đại diện

    @Column(name = "author", nullable = false)
    private String author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // Liên kết danh mục (nếu có)


}
