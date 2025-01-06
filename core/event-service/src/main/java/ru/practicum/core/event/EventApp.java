package ru.practicum.core.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.client.StatClient;
import ru.practicum.core.api.client.LikeServiceClient;
import ru.practicum.core.api.client.LocationServiceClient;
import ru.practicum.core.api.client.RequestServiceClient;
import ru.practicum.core.api.client.UserServiceClient;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.core.event", "ru.practicum.client"})
@EnableFeignClients(clients = {
        StatClient.class, UserServiceClient.class, LocationServiceClient.class,
        LikeServiceClient.class, RequestServiceClient.class})
public class EventApp {

    public static void main(String[] args) {
        SpringApplication.run(EventApp.class, args);

    }
}
