// src/main/java/com/mytech/virtualcourse/controllers/TicketController.java

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TicketDTO;
import com.mytech.virtualcourse.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Tạo ticket (support request)
    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO dto) {
        TicketDTO created = ticketService.createTicket(dto);
        return ResponseEntity.ok(created);
    }

    // Lấy tất cả ticket
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        List<TicketDTO> list = ticketService.getAllTickets();
        return ResponseEntity.ok(list);
    }

    // Lấy ticket theo id
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long ticketId) {
        TicketDTO dto = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(dto);
    }

    // Mark as resolved
    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<String> resolveTicket(@PathVariable Long ticketId) {
        ticketService.resolveTicket(ticketId);
        return ResponseEntity.ok("Ticket resolved successfully.");
    }

    // Đóng ticket
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<String> closeTicket(@PathVariable Long ticketId) {
        ticketService.closeTicket(ticketId);
        return ResponseEntity.ok("Ticket closed successfully.");
    }
}
