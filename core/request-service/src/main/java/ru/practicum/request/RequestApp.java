package ru.practicum.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.practicum.core.api.client.FeignClientConfig;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.request","ru.practicum.client"})
@Import({FeignClientConfig.class})
public class RequestApp {
    public static void main(String[] args) {
        SpringApplication.run(RequestApp.class, args);
    }
}
