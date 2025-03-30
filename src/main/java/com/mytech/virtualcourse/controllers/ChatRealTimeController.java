package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.ChatMessage;
import com.mytech.virtualcourse.mappers.ChatMessageMapper;
import com.mytech.virtualcourse.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class ChatRealTimeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageDTO chatMessageDTO) {
        // Lưu tin nhắn
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);

        // Convert sang DTO đầy đủ
        ChatMessageDTO dto = chatMessageMapper.toDTO(savedMessage);

        // Gửi tin nhắn đến người nhận qua /queue/user.{receiverAccountId}
        String destination = "/queue/user." + savedMessage.getReceiverAccount().getId();
        messagingTemplate.convertAndSend(destination, dto);
    }
}
