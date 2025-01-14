package ru.yandex.practicum.stats.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.analyzer.configuration.KafkaAnalyzerConfig;
import ru.yandex.practicum.stats.analyzer.service.UserActionService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class UserActionProcessor implements Runnable {

    private final KafkaConsumer<String, UserActionAvro> userActionsKafkaConsumer;
    private final KafkaAnalyzerConfig kafkaConfig;

    private final UserActionService userActionService;

    public UserActionProcessor(KafkaAnalyzerConfig kafkaConfig,
                               UserActionService userActionService) {
        this.kafkaConfig = kafkaConfig;
        this.userActionService = userActionService;
        userActionsKafkaConsumer = new KafkaConsumer<>(kafkaConfig.getUserActionsProperties());
    }


    @Override
    public void run() {
        log.info("Starting UserActionsProcessor");

        try (userActionsKafkaConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(userActionsKafkaConsumer::wakeup));
            userActionsKafkaConsumer.subscribe(List.of(kafkaConfig.getTopics().get("user-actions")));

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = userActionsKafkaConsumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    UserActionAvro userAction = record.value();
                    userActionService.save(userAction);
                }
                userActionsKafkaConsumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Commit processing failed. Offsets: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Analyzer. Error by handling userAction from Kafka", e);
        } finally {
            log.info("Analyzer. Closing userActionsProcessor");
        }

    }
}
