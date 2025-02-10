package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.ChatMessage;
import com.mytech.virtualcourse.mappers.ChatMessageMapper;
import com.mytech.virtualcourse.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @GetMapping("/history")
    public List<ChatMessageDTO> getChatHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        List<ChatMessage> messages = chatService.getChatHistory(user1Id, user2Id);
        return messages.stream().map(chatMessageMapper::toDTO).toList();
    }
}
