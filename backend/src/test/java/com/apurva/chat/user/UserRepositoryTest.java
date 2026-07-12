package com.apurva.chat.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Repository slice test. @DataJpaTest spins up ONLY the JPA layer against an
 * in-memory H2 database — fast, and needs no real Postgres/Kafka.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository users;

    @Test
    void savesAndFindsUserByUsername() {
        users.save(new AppUser("neo", "hashed-pw", "Neo"));

        assertTrue(users.existsByUsername("neo"));
        assertTrue(users.findByUsername("neo").isPresent());
        assertFalse(users.findByUsername("nobody").isPresent());
    }
}
