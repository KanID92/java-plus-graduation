package ru.yandex.practicum.stats.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.analyzer.configuration.KafkaAnalyzerConfig;
import ru.yandex.practicum.stats.analyzer.service.EventSimilarityService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class EventSimilarityProcessor implements Runnable {

    private final KafkaConsumer<String, EventSimilarityAvro> eventSimilarityConsumer;
    private final KafkaAnalyzerConfig kafkaConfig;

    private final EventSimilarityService eventSimilarityService;

    public EventSimilarityProcessor(KafkaAnalyzerConfig config,
                                    EventSimilarityService eventSimilarityService) {
        this.kafkaConfig = config;
        this.eventSimilarityService = eventSimilarityService;
        eventSimilarityConsumer = new KafkaConsumer<>(kafkaConfig.getEventSimilarityProperties());
    }

    @Override
    public void run() {
        log.info("Starting event similarity processor...");
        try (eventSimilarityConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(eventSimilarityConsumer::wakeup));
            eventSimilarityConsumer.subscribe(List.of(kafkaConfig.getTopics().get("event-similarity")));

            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records =
                        eventSimilarityConsumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    EventSimilarityAvro eventSimilarityAvro = record.value();
                    eventSimilarityService.save(eventSimilarityAvro);
                }
                eventSimilarityConsumer.commitAsync((offset, exception) -> {
                    if (exception != null) {
                        log.warn("Commit processing failed. Offsets: {}", offset, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Analyzer. Error by handling eventSimilarity from Kafka", e);
        } finally {
            log.info("Analyzer. Closing eventSimilarity processor");
        }

    }
}
