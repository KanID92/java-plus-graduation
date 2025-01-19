package ru.yandex.practicum.stats.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.stats.actions.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.actions.UserActionProto;
import ru.yandex.practicum.stats.collector.configuration.KafkaUserActionProducer;

import java.time.Instant;

@Slf4j
@Component
public class UserActionHandlerImpl extends BaseUserActionHandler {

    public UserActionHandlerImpl(KafkaUserActionProducer kafkaUserActionProducer) {
        super(kafkaUserActionProducer);
    }

    @Override
    SpecificRecordBase toAvro(UserActionProto userAction) {
        log.info("Converting to Avro UserActionProto");
        return UserActionAvro.newBuilder()
                .setUserId(userAction.getUserId())
                .setEventId(userAction.getEventId())
                .setActionType(toActionTypeAvro(userAction.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(userAction.getTimestamp().getSeconds(), userAction.getTimestamp().getNanos()))
                .build();
    }

    private ActionTypeAvro toActionTypeAvro(ActionTypeProto userTypeProto) {
        return switch (userTypeProto) {
            case ActionTypeProto.ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ActionTypeProto.ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ActionTypeProto.ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            default -> null;
        };
    }


}