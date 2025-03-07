package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.ChatMessageDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.ChatMessage;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ChatMessage saveMessage(ChatMessageDTO dto) {
        Account sender = accountRepository.findById(dto.getSenderAccountId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account receiver = accountRepository.findById(dto.getReceiverAccountId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        ChatMessage msg = new ChatMessage();
        msg.setSenderAccount(sender);
        msg.setReceiverAccount(receiver);
        msg.setContent(dto.getContent());
        msg.setTimestamp(OffsetDateTime.now(ZoneOffset.of("+07:00")));
        return chatMessageRepository.save(msg);
    }

    public List<ChatMessage> getChatHistory(Long user1AccId, Long user2AccId) {
        return chatMessageRepository.findBySenderAccountIdAndReceiverAccountIdOrReceiverAccountIdAndSenderAccountId(
                user1AccId, user2AccId, user1AccId, user2AccId
        );
    }

    public List<Long> getRecentChats(Long userId) {
        return chatMessageRepository.findRecentChatsByUserId(userId);
    }

    public List<Long> getRecentChatsForInstructor(Long instructorId) {
        return chatMessageRepository.findRecentChatsByInstructorId(instructorId);
    }
}