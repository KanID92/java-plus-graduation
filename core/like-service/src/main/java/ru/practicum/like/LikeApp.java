package ru.practicum.like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.core.api.client.EventServiceClient;
import ru.practicum.core.api.client.LocationServiceClient;
import ru.practicum.core.api.client.RequestServiceClient;


@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.like", "ru.practicum.client"})
@EnableFeignClients(clients = {EventServiceClient.class, LocationServiceClient.class, RequestServiceClient.class})
public class LikeApp {
    public static void main(String[] args) {
        SpringApplication.run(LikeApp.class, args);
    }
}
