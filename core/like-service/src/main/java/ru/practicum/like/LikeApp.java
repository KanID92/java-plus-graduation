package ru.practicum.like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@EnableFeignClients
public class LikeApp {
    public static void main(String[] args) {
        SpringApplication.run(LikeApp.class, args);
    }
}
