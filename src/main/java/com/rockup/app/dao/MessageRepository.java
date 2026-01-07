package com.rockup.app.dao;

import com.rockup.app.model.entity.MessageEntity;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select coalesce(max(m.messageChatN), 0)
        from MessageEntity m
        where m.chatId = :chatId
    """)
    int findLastMessageNumberForUpdate(UUID chatId);

    Page<MessageEntity> findByChatIdAndUserIdOrderByMessageChatNAsc(UUID chatId, UUID userId, Pageable pageable);
}
