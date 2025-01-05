package ru.practicum.like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.core.api.client.EventServiceClient;
import ru.practicum.core.api.client.LocationServiceClient;


@SpringBootApplication
@EnableFeignClients(clients = {EventServiceClient.class, LocationServiceClient.class})
public class LikeApp {
    public static void main(String[] args) {
        SpringApplication.run(LikeApp.class, args);
    }
}
