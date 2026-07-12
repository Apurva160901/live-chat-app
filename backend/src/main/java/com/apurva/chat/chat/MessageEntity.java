package com.apurva.chat.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A chat message as stored in the database.
 *
 * @Entity tells Hibernate/JPA "map this class to a database table". Each field
 * becomes a column; each object becomes a row. This is the "O" and "R" in ORM
 * (Object-Relational Mapping): Java Objects <-> Relational table rows.
 */
@Entity
@Table(name = "messages")
public class MessageEntity {

    /** Primary key. IDENTITY = let PostgreSQL auto-generate an incrementing id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    /** JPA requires a no-argument constructor (it builds objects via reflection). */
    protected MessageEntity() {
    }

    public MessageEntity(String sender, String content, Instant timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
