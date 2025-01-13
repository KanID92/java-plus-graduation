package ru.yandex.practicum.stats.collector.service.handler;

import ru.yandex.practicum.grpc.stats.user.UserActionProto;

public interface UserActionHandler {

    void handle(UserActionProto userActionProto);

}
