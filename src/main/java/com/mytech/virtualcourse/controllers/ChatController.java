package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.ChatMessage;
import com.mytech.virtualcourse.mappers.ChatMessageMapper;
import com.mytech.virtualcourse.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        List<ChatMessage> messages = chatService.getChatHistory(user1Id, user2Id);
        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(chatMessageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }

    @GetMapping("/recent-chats")
    public ResponseEntity<List<Long>> getRecentChats(@RequestParam Long userId) {
        List<Long> chatList = chatService.getRecentChats(userId);
        return ResponseEntity.ok(chatList);
    }

    @GetMapping("/recent-chats-instructor")
    public ResponseEntity<List<Long>> getRecentChatsForInstructor(@RequestParam Long instructorId) {
        List<Long> chatList = chatService.getRecentChatsForInstructor(instructorId);
        return ResponseEntity.ok(chatList);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<ChatMessageDTO> sendChatMessage(@RequestBody ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);
        ChatMessageDTO result = chatMessageMapper.toDTO(savedMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getReceiverAccountId().toString(),
                "/queue/user",
                result
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}