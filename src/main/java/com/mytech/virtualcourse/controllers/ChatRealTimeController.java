package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.ChatMessage;
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

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageDTO chatMessageDTO) {
        // Lưu tin nhắn vào cơ sở dữ liệu
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);

        // Gửi tin nhắn đến người nhận
        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getReceiverAccountId().toString(),
                "/queue/user",
                chatMessageDTO
        );
    }
}
