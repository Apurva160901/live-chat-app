package com.apurva.chat.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Declares our Kafka topic(s). Spring auto-creates them on startup. */
@Configuration
public class KafkaTopics {

    public static final String CHAT_MESSAGES = "chat.messages";

    @Bean
    public NewTopic chatMessagesTopic() {
        return TopicBuilder.name(CHAT_MESSAGES).partitions(1).replicas(1).build();
    }
}
