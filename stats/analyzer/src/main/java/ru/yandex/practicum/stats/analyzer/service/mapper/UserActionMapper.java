package ru.yandex.practicum.stats.analyzer.service.mapper;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.analyzer.entity.UserAction;

public interface UserActionMapper {

    UserAction toUserAction(final UserActionAvro actionAvro);
}
