// src/main/java/com/mytech/virtualcourse/mappers/TicketMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TicketDTO;
import com.mytech.virtualcourse.entities.SupportTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketMapper {

    @Mapping(source = "creator.id", target = "creatorAccountId")
    @Mapping(source = "creator.email", target = "creatorEmail")
    @Mapping(source = "assignedTo.id", target = "assignedToAccountId")
    @Mapping(source = "assignedTo.email", target = "assignedToEmail")
    TicketDTO toDTO(SupportTicket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "creatorAccountId", target = "creator.id")
    @Mapping(source = "assignedToAccountId", target = "assignedTo.id")
    SupportTicket toEntity(TicketDTO ticketDTO);
}
