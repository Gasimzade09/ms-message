package com.rockup.app.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "message",
        indexes = {
                @Index(name = "message_ux1", columnList = "chat_id, message_chat_n DESC, version DESC", unique = true)
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEntity {
    @Id
    @Column(nullable = false, updatable = false)
    UUID id;

    @Column(name = "user_id", nullable = false)
    UUID userId;

    @Column(name = "chat_id", nullable = false)
    UUID chatId;

    @Column(name = "message_chat_n", nullable = false)
    Integer messageChatN;

    @Version
    @Column(nullable = false)
    Integer version;

    @Column(nullable = false)
    String payload;

    public MessageEntity(UUID id, UUID userId, UUID chatId, Integer messageChatN, String payload) {
        this.id = id;
        this.userId = userId;
        this.chatId = chatId;
        this.messageChatN = messageChatN;
        this.payload = payload;
    }
}
