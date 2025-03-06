package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAccountIdAndReceiverAccountIdOrReceiverAccountIdAndSenderAccountId(
            Long senderAccId, Long receiverAccId, Long receiverAccId2, Long senderAccId2
    );

    @Query("SELECT DISTINCT CASE WHEN cm.senderAccount.id = :userId THEN cm.receiverAccount.id ELSE cm.senderAccount.id END FROM ChatMessage cm WHERE cm.senderAccount.id = :userId OR cm.receiverAccount.id = :userId")
    List<Long> findRecentChatsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT CASE WHEN cm.senderAccount.id = :instructorId THEN cm.receiverAccount.id ELSE cm.senderAccount.id END FROM ChatMessage cm WHERE cm.senderAccount.id = :instructorId OR cm.receiverAccount.id = :instructorId")
    List<Long> findRecentChatsByInstructorId(@Param("instructorId") Long instructorId);
}