package com.apurva.chat.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A registered user, stored in the "users" table.
 *
 * The password is NEVER stored in plain text — we store a BCrypt hash.
 * (Named AppUser instead of "User" because "user" is a reserved word in SQL.)
 */
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique login name. */
    @Column(nullable = false, unique = true)
    private String username;

    /** BCrypt-hashed password (never plain text). */
    @Column(nullable = false)
    private String password;

    /** Name shown in the UI. */
    @Column(nullable = false)
    private String displayName;

    /** URL/path of the uploaded profile picture (null until they upload one). */
    @Column
    private String avatarUrl;

    /** Email address, collected at registration; used for password-reset emails. */
    @Column
    private String email;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected AppUser() {
    }

    public AppUser(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
