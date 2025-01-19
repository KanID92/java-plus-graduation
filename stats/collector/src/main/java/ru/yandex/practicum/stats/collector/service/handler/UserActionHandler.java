package ru.yandex.practicum.stats.collector.service.handler;

import ru.yandex.practicum.grpc.stats.actions.UserActionProto;

public interface UserActionHandler {

    void handle(UserActionProto userActionProto);

}
