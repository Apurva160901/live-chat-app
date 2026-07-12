package com.apurva.chat.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data access for messages.
 *
 * By extending JpaRepository, Spring Data AUTO-GENERATES the implementation at
 * runtime — we get save(), findAll(), findById(), delete(), etc. for free, no SQL.
 *
 * We can also declare "derived query" methods: Spring reads the METHOD NAME and
 * writes the query for us. The name below means:
 *   "find the top 50 messages, ordered by timestamp ascending".
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findTop50ByOrderByTimestampAsc();
}
