package ru.yandex.practicum.stats.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class KafkaConfig {

    @Value("${collector.kafka.topics.user-actions}")
    private String userActionsTopic;

    @Value("${collector.kafka.producer-properties.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${collector.kafka.producer-properties.key.serializer}")
    private String keySerializer;

    @Value("${collector.kafka.producer-properties.value.serializer}")
    private String valueSerializer;


}
