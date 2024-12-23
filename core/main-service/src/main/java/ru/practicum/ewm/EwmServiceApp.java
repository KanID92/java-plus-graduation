package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.ewm", "ru.practicum.client"})
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class EwmServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(EwmServiceApp.class, args);

    }
}
