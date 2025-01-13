package ru.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.user.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.user.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.user.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendUserAction(long userId, long eventId, String userAction) {
        try {
            log.info("Sending userAction. UserId {}, eventId {}, userAction {}", userId, eventId, userAction);
            UserActionProto actionProto = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(toActionTypeProto(userAction))
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(Instant.now().getEpochSecond())
                            .setNanos(Instant.now().getNano())
                            .build())
                    .build();
            Empty response = collectorStub.collectUserAction(actionProto);
            log.info("UserAction sent: {}", actionProto);
        } catch (Exception e) {
            log.error("Error sending userActionProto: {}, {}, {}", userId, eventId, userAction, e);
        }
    }

    private ActionTypeProto toActionTypeProto(String actionType) {
        switch (actionType) {
            case "VIEW" -> {
                return ActionTypeProto.ACTION_VIEW;
            }
            case "REGISTER" -> {
                return ActionTypeProto.ACTION_REGISTER;
            }
            case "LIKE" -> {
                return ActionTypeProto.ACTION_LIKE;
            }
            default -> throw new IllegalStateException("Unexpected value: " + actionType);
        }
    }
}
