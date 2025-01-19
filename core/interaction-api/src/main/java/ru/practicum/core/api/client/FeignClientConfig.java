package ru.practicum.core.api.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.practicum.core.api.client")
public class FeignClientConfig {
}
