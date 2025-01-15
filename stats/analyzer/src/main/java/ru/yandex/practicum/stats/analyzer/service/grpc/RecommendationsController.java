package ru.yandex.practicum.stats.analyzer.service.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.stats.recommendations.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProtoOuterClass;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProtoOuterClass;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProtoOuterClass;
import ru.yandex.practicum.grpc.stats.user.UserPredictionsRequestProtoOuterClass;
import ru.yandex.practicum.stats.analyzer.entity.RecommendedEvent;
import ru.yandex.practicum.stats.analyzer.service.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProtoOuterClass.UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProtoOuterClass.RecommendedEventProto> responseObserver) {

        try {
            List<RecommendedEvent> recommendedEventsList =
                    recommendationService.getRecommendedEventsForUser(request.getUserId(), request.getMaxResults());

            for (RecommendedEvent recommendedEvent : recommendedEventsList) {
                RecommendedEventProtoOuterClass.RecommendedEventProto responseProto =
                        RecommendedEventProtoOuterClass.RecommendedEventProto.newBuilder()
                                .setEventId(recommendedEvent.getEventId())
                                .setScore(recommendedEvent.getScore())
                                .build();
                responseObserver.onNext(responseProto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Method. getRecommendationsForUser. Illegal argument: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e)));
        } catch (Exception e) {
            log.error("Method getRecommendationsForUser. Unknown Error: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage()).withCause(e)));
        }

    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProtoOuterClass.SimilarEventsRequestProto similarEventsRequestProto,
            StreamObserver<RecommendedEventProtoOuterClass.RecommendedEventProto> responseObserver
    ) {
        try {
            List<RecommendedEvent> recommendedEventList =
                    recommendationService.getSimilarEvents(
                            similarEventsRequestProto.getEventId(),
                            similarEventsRequestProto.getUserId(),
                            similarEventsRequestProto.getMaxResults());

            for (RecommendedEvent recommendedEvent : recommendedEventList) {
                RecommendedEventProtoOuterClass.RecommendedEventProto responseProto =
                        RecommendedEventProtoOuterClass.RecommendedEventProto.newBuilder()
                                .setEventId(recommendedEvent.getEventId())
                                .setScore(recommendedEvent.getScore())
                                .build();
                responseObserver.onNext(responseProto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Method. getSimilarEvents. Illegal argument: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e)));
        } catch (Exception e) {
            log.error("Method getSimilarEvents. Unknown Error: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage()).withCause(e)));
        }
    }

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProtoOuterClass.InteractionsCountRequestProto interactionsCountRequestProto,
            StreamObserver<RecommendedEventProtoOuterClass.RecommendedEventProto> responseObserver
    ) {
        try {
            List<RecommendedEvent> recommendedEventList =
                    recommendationService.getInteractionsCount(interactionsCountRequestProto.getEventIdList());
            for (RecommendedEvent recommendedEvent : recommendedEventList) {
                RecommendedEventProtoOuterClass.RecommendedEventProto responseProto =
                        RecommendedEventProtoOuterClass.RecommendedEventProto.newBuilder()
                                .setEventId(recommendedEvent.getEventId())
                                .setScore(recommendedEvent.getScore())
                                .build();
                responseObserver.onNext(responseProto);
            }
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Method getInteractionsCount. Illegal argument: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e)));
        } catch (Exception e) {
            log.error("Method getInteractionsCount. Unknown error: {}", e.getMessage());
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage()).withCause(e)));
        }

    }



}
