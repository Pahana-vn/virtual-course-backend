package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.ChatMessage;
import com.mytech.virtualcourse.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatTestController {

    @Autowired
    private ChatService chatService;

    // Endpoint để test post dữ liệu chat mà không cần qua WebSocket
    @PostMapping("/test-send")
    public ResponseEntity<ChatMessageDTO> testSendMessage(@RequestBody ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);
        // Chuyển entity sang DTO
        ChatMessageDTO result = new ChatMessageDTO();
        result.setSenderAccountId(savedMessage.getSenderAccount().getId());
        result.setReceiverAccountId(savedMessage.getReceiverAccount().getId());
        result.setContent(savedMessage.getContent());
        result.setType("CHAT");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
