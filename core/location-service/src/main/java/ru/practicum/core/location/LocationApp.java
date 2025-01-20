package ru.practicum.core.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import ru.practicum.core.api.client.FeignClientConfig;


@SpringBootApplication
@EnableDiscoveryClient
@Import({FeignClientConfig.class})
public class LocationApp {
    public static void main(String[] args) {
        SpringApplication.run(LocationApp.class, args);
    }
}
