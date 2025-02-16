package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TicketDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.SupportTicket;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.TicketMapper;
import com.mytech.virtualcourse.repositories.AdminAccountRepository;
import com.mytech.virtualcourse.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AdminAccountRepository accountRepository;

    @Autowired
    private TicketMapper ticketMapper;

    public TicketDTO createTicket(TicketDTO dto) {
        Account creator = accountRepository.findById(dto.getCreatorAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + dto.getCreatorAccountId()));

        SupportTicket ticket = ticketMapper.toEntity(dto);
        ticket.setCreator(creator);
        ticket.setStatus("OPEN");

        SupportTicket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDTO(savedTicket);
    }

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Long ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        return ticketMapper.toDTO(ticket);
    }

    public void resolveTicket(Long ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        ticket.setStatus("RESOLVED");
        ticketRepository.save(ticket);
    }

    public void closeTicket(Long ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
        ticket.setStatus("CLOSED");
        ticketRepository.save(ticket);
    }
}
