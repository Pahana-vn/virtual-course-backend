package com.mytech.virtualcourse.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED, ...

    @ManyToOne
    @JoinColumn(name = "creator_account_id", nullable = false)
    private Account creator; // Người tạo ticket

    @ManyToOne
    @JoinColumn(name = "assigned_to_account_id")
    private Account assignedTo; // Người được gán xử lý ticket
}
