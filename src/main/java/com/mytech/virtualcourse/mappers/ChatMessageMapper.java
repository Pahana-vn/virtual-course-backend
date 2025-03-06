package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.ChatMessage;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class ChatMessageMapper {

    private String getAccountName(Account account) {
        if (account.getInstructor() != null) {
            return account.getInstructor().getFirstName() + " " + account.getInstructor().getLastName();
        } else if (account.getStudent() != null) {
            return account.getStudent().getFirstName() + " " + account.getStudent().getLastName();
        }
        return "Unknown User";
    }

    private String getAccountAvatar(Account account) {
        String baseUrl = "http://localhost:8080/uploads/";
        if (account.getInstructor() != null) {
            String photo = account.getInstructor().getPhoto();
            if (photo != null && !photo.isEmpty()) {
                return baseUrl + "instructor/" + photo;
            }
        } else if (account.getStudent() != null) {
            String avatar = account.getStudent().getAvatar();
            if (avatar != null && !avatar.isEmpty()) {
                return baseUrl + "student/" + avatar;
            }
        }
        return null;
    }

    public ChatMessageDTO toDTO(ChatMessage msg) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderAccountId(msg.getSenderAccount().getId());
        dto.setReceiverAccountId(msg.getReceiverAccount().getId());
        dto.setContent(msg.getContent());
        dto.setType("CHAT");
        dto.setTimestamp(msg.getTimestamp()); // giờ là OffsetDateTime

        dto.setSenderName(getAccountName(msg.getSenderAccount()));
        dto.setSenderAvatar(getAccountAvatar(msg.getSenderAccount()));

        dto.setReceiverName(getAccountName(msg.getReceiverAccount()));
        dto.setReceiverAvatar(getAccountAvatar(msg.getReceiverAccount()));
        return dto;
    }

    public ChatMessage toEntity(ChatMessageDTO dto, Account sender, Account receiver) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderAccount(sender);
        msg.setReceiverAccount(receiver);
        msg.setContent(dto.getContent());
        // Sử dụng OffsetDateTime UTC
        msg.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return msg;
    }
}