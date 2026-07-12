package com.apurva.chat.dm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the custom conversation() query returns messages in BOTH directions
 * between two users, in time order, and excludes other conversations.
 */
@DataJpaTest
class DirectMessageRepositoryTest {

    @Autowired
    private DirectMessageRepository repo;

    @Test
    void loadsConversationBetweenTwoUsersOnly() {
        Instant now = Instant.now();
        repo.save(new DirectMessage("alice", "bob", "hi bob", now));
        repo.save(new DirectMessage("bob", "alice", "hey alice", now.plusSeconds(1)));
        repo.save(new DirectMessage("alice", "carol", "unrelated", now.plusSeconds(2)));

        List<DirectMessage> conversation = repo.conversation("alice", "bob");

        assertEquals(2, conversation.size());                 // only alice<->bob
        assertEquals("hi bob", conversation.get(0).getContent());   // oldest first
        assertEquals("hey alice", conversation.get(1).getContent());
    }
}
