package ru.yandex.practicum.stats.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.aggregator.configuration.kafka.KafkaConfig;
import ru.yandex.practicum.stats.aggregator.repository.EventSimilarityRepository;
import ru.yandex.practicum.stats.aggregator.repository.EventSimilarityRepositoryInMemory;
import ru.yandex.practicum.stats.aggregator.repository.UserActionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeneralAggregatorService implements AggregatorService {

    private final KafkaConfig kafkaConfig;
    private final KafkaProducer<Long, EventSimilarityAvro> producer;
    private final KafkaConsumer<Long, UserActionAvro> consumer;
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;


    public GeneralAggregatorService(KafkaConfig kafkaConfig,
                                    UserActionRepository userActionRepository,
                                    EventSimilarityRepository eventSimilarityRepository, EventSimilarityRepositoryInMemory eventSimilarityRepositoryInMemory) {
        this.kafkaConfig = kafkaConfig;
        this.producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumerProperties());
        this.userActionRepository = userActionRepository;
        this.eventSimilarityRepository = eventSimilarityRepository;
    }

    public void start() {
        try (consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(kafkaConfig.getTopics().get("user-actions")));

            //poll loop - цикл опроса
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records =
                        consumer.poll(Duration.ofSeconds(10));
                handleReceivedRecords(records);

            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Aggregator. Error by handling userAction from Kafka", e);
        } finally {
            log.info("Aggregator. Closing GeneralAggregatorService");
        }
    }

    private void handleReceivedRecords(ConsumerRecords<Long, UserActionAvro> records) {
        for (ConsumerRecord<Long, UserActionAvro> record : records) {
            UserActionAvro userAction = record.value();
            Long eventId = userAction.getEventId();

            // Получение всех активностей пользователей по событию
            Map<Long, Double> usersActionsMap = userActionRepository.getActionsByEvent(eventId);

            List<EventSimilarityAvro> similarities;

            if (usersActionsMap.isEmpty()) {
                //если событие - новое
                // Сохранение веса активности пользователя для события
                userActionRepository.save(userAction);
                //Расчет сходства с остальными событиями (полный)
                similarities = getSimilaritiesForNewEvent(userAction);
            } else {
                //если событие - yt новое
                similarities = getSimilarities(userAction);
            }

            if (!similarities.isEmpty()) {
                for (EventSimilarityAvro eventSimilarityAvro : similarities) {
                    sendSimilarity(eventSimilarityAvro);
                }
            }
        }
    }

    private List<EventSimilarityAvro> getSimilarities(UserActionAvro userActionAvro) {
        List<EventSimilarityAvro> similarities;

        long eventIdA = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();
        ActionTypeAvro actionTypeAvro = userActionAvro.getActionType();
        double receivedWeight = userActionRepository.getWeightFromAvro(actionTypeAvro);
        double oldWeight = userActionRepository.getActionsByEvent(eventIdA).getOrDefault(userId,0.0);

        if (receivedWeight > oldWeight) {
            userActionRepository.save(userActionAvro);
            similarities = countSimilarities(eventIdA, userId, oldWeight, receivedWeight);
        } else {
            return List.of();
        }

        return similarities;
    }

    private List<EventSimilarityAvro> getSimilaritiesForNewEvent(UserActionAvro userActionAvro) {
        List<EventSimilarityAvro> similarities = new ArrayList<>();

        long eventIdA = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();
        double eventWeight = userActionRepository.getActionsByEvent(eventIdA).get(userId);
        List<Long> allEventIds = eventSimilarityRepository.getAllEventIds();

        for (long eventIdB : allEventIds) {
            double minSums = countFirstMinSum(eventIdA, eventIdB);
            //Сохранение минимальной суммы
            eventSimilarityRepository.putMinWeightSum(eventIdA, eventIdB, minSums);
            //Сохранение полной суммы
            eventSimilarityRepository.putEventTotalSum(eventIdA,eventWeight);
            double totalSumB = eventSimilarityRepository.getEventTotalSum(eventIdB);
            double similarityAB = minSums / (Math.sqrt(eventWeight) * Math.sqrt(totalSumB));
            similarities.add(EventSimilarityAvro.newBuilder()
                    .setEventA(Math.min(eventIdA, eventIdB))
                    .setEventB(Math.max(eventIdA, eventIdB))
                    .setScore(similarityAB)
                    .setTimestamp(Instant.now())
                    .build());
        }
        return similarities;
    }



    private List<EventSimilarityAvro> countSimilarities(long eventIdA,
                                                        long userId,
                                                        double oldWeight,
                                                        double receivedWeight) {
        List<EventSimilarityAvro> similarities = new ArrayList<>();
        for (long eventIdB : eventSimilarityRepository.getAllEventIds()) {
            double oldMinSum = eventSimilarityRepository.getMinWeightSum(eventIdA, eventIdB);
            double newMinSum;

            double eventBweight =
                    userActionRepository.getActionsByEvent(eventIdB).getOrDefault(userId, 0.0);
            if (receivedWeight <=  eventBweight) {
                newMinSum = oldMinSum;
            } else {
                newMinSum = oldMinSum - eventBweight + receivedWeight;
                //Сохранение нового значения minWeightSum
                eventSimilarityRepository.putMinWeightSum(eventIdA, eventIdB,newMinSum);
            }

            double eventATotalSum = eventSimilarityRepository.getEventTotalSum(eventIdA) - oldWeight + receivedWeight;
            //Сохранение нового значения totalSum
            eventSimilarityRepository.putEventTotalSum(eventIdA, eventATotalSum);
            double eventBTotalSum = eventSimilarityRepository.getEventTotalSum(eventIdB);

            double similarityAB = newMinSum / (Math.sqrt(eventATotalSum) * Math.sqrt(eventBTotalSum));

            similarities.add(EventSimilarityAvro.newBuilder()
                    .setEventA(Math.min(eventIdA, eventIdB))
                    .setEventB(Math.max(eventIdA, eventIdB))
                    .setScore(similarityAB)
                    .setTimestamp(Instant.now())
                    .build());
        }
        return similarities;
    }

    private double countFirstMinSum(long eventA, long eventB) {
        double minSum = 0;

        Map<Long, Double> usersActionsOfEventA = userActionRepository.getActionsByEvent(eventA);
        Map<Long, Double> usersActionsOfEventB = userActionRepository.getActionsByEvent(eventB);

        for (long userId : usersActionsOfEventA.keySet()) {
            minSum += Math.min(
                    usersActionsOfEventA.get(userId), usersActionsOfEventB.getOrDefault(userId, 0.0));
        }

        return minSum;
    }

    private void sendSimilarity(EventSimilarityAvro similarityAvro) {
        log.info("Sending similarityAvro for events: {}, {}. Similarity: {}",
                similarityAvro.getEventA(), similarityAvro.getEventB(), similarityAvro.getScore());
        ProducerRecord<Long, EventSimilarityAvro> similarityRecord =
                new ProducerRecord<>(
                        kafkaConfig.getTopics().get("sensors-snapshots"), null, null, similarityAvro);
        log.info("Sending similarityAvro {}", similarityRecord);
        try (producer) {
            producer.send(similarityRecord);
            producer.flush();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

