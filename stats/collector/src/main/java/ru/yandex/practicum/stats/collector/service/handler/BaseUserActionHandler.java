package ru.yandex.practicum.stats.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.stats.actions.UserActionProto;
import ru.yandex.practicum.stats.collector.configuration.KafkaUserActionProducer;

@Slf4j
public abstract class BaseUserActionHandler implements UserActionHandler {

    KafkaUserActionProducer producer;
    String topic;

    public BaseUserActionHandler(KafkaUserActionProducer kafkaProducer) {
        this.producer = kafkaProducer;
        topic = kafkaProducer.getConfig().getUserActionsTopic();
    }

    @Override
    public void handle(UserActionProto userAction) {
        log.info("Handling user action {}", userAction);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic, null, System.currentTimeMillis(), null, toAvro(userAction));
        log.info("Sending {} to topic {}", userAction, topic);
        producer.sendRecord(record);
   }

   abstract SpecificRecordBase toAvro(UserActionProto userAction);

}
