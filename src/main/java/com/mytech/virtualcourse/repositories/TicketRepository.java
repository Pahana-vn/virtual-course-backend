package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<SupportTicket, Long> {
}
