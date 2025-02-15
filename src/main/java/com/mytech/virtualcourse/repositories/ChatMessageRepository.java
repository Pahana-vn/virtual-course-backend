package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAccountIdAndReceiverAccountIdOrReceiverAccountIdAndSenderAccountId(
            Long senderAccId, Long receiverAccId, Long receiverAccId2, Long senderAccId2
    );
}
