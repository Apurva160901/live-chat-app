package com.apurva.chat.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    /** All messages exchanged between two users, oldest first. */
    @Query("select d from DirectMessage d "
            + "where (d.sender = :a and d.recipient = :b) "
            + "   or (d.sender = :b and d.recipient = :a) "
            + "order by d.timestamp asc")
    List<DirectMessage> conversation(@Param("a") String a, @Param("b") String b);
}
