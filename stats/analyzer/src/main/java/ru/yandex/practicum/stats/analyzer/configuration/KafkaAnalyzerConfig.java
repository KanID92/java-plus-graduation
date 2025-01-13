package ru.yandex.practicum.stats.analyzer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("analyzer.kafka")
public class KafkaAnalyzerConfig {

    private Map<String, String> topics;
    private Properties userActionsProperties;
    private Properties eventSimilarityProperties;

}
