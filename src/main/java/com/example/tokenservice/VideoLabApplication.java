package com.example.tokenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VideoLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoLabApplication.class, args);
    }
}
