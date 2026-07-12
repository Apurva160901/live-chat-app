package com.apurva.chat.dm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * One private message from `sender` to `recipient`.
 * May carry a text `content` and/or an attachment (image/file).
 */
@Entity
@Table(name = "direct_messages", indexes = {
        @Index(name = "idx_dm_pair", columnList = "sender,recipient")
})
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    /** Attachment (optional): public URL, type ("IMAGE"/"FILE"), and original file name. */
    @Column
    private String attachmentUrl;

    @Column
    private String attachmentType;

    @Column
    private String attachmentName;

    protected DirectMessage() {
    }

    public DirectMessage(String sender, String recipient, String content, Instant timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }
}
