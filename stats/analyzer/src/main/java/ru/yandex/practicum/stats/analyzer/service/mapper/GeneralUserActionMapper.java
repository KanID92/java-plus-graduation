package ru.yandex.practicum.stats.analyzer.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.analyzer.entity.ActionType;
import ru.yandex.practicum.stats.analyzer.entity.UserAction;

@Component
public class GeneralUserActionMapper implements UserActionMapper {

    @Override
    public UserAction toUserAction(UserActionAvro actionAvro) {
        return UserAction.builder()
                .userId(actionAvro.getUserId())
                .eventId(actionAvro.getEventId())
                .actionType(toActionType(actionAvro.getActionType()))
                .timestamp(actionAvro.getTimestamp())
                .build();
    }

    private ActionType toActionType(ActionTypeAvro actionTypeAvro) {
        switch (actionTypeAvro) {
            case VIEW -> {
                return ActionType.VIEW;
            }
            case REGISTER -> {
                return ActionType.REGISTER;
            }
            case LIKE -> {
                return ActionType.LIKE;
            }
            default -> throw new RuntimeException("actionTypeAvro: " + actionTypeAvro + " is not supported");
        }
    }
}
