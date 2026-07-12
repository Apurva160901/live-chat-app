package com.apurva.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The single entry point of the whole backend.
 *
 * @SpringBootApplication is a shortcut for three annotations:
 *   - @Configuration      : this class can define beans (objects Spring manages)
 *   - @EnableAutoConfiguration : Spring auto-configures things it finds on the classpath
 *                                (e.g. sees the web starter -> starts a Tomcat web server)
 *   - @ComponentScan      : finds our @Controller / @Service / @Repository classes
 *                           in this package and sub-packages, and wires them together.
 */
@SpringBootApplication
public class ChatBackendApplication {

    public static void main(String[] args) {
        // This one line boots the entire application: starts the web server,
        // connects to the database, and registers all our components.
        SpringApplication.run(ChatBackendApplication.class, args);
    }
}
