package ru.yandex.practicum.stats.analyzer.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.stats.analyzer.entity.UserAction;

public interface UserActionService {

    UserAction save(UserActionAvro userActionAvro);

}
