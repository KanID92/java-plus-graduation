package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.recommendations.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.request.InteractionsCountRequestProtoOuterClass;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProtoOuterClass;
import ru.yandex.practicum.grpc.stats.request.SimilarEventsRequestProtoOuterClass;
import ru.yandex.practicum.grpc.stats.user.UserPredictionsRequestProtoOuterClass;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    public Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> getRecommendedEventsForUser(
            long userId, int size) {
        try {
            log.info("AnalyzerClient. Getting recommendation. UserId: {}, size: {}", userId, size);
            UserPredictionsRequestProtoOuterClass.UserPredictionsRequestProto predictionsRequestProto =
                    UserPredictionsRequestProtoOuterClass.UserPredictionsRequestProto.newBuilder()
                            .setUserId(userId)
                            .setMaxResults(size)
                            .build();
            Iterator<RecommendedEventProtoOuterClass.RecommendedEventProto> responseIterator =
                    analyzerStub.getRecommendationsForUser(predictionsRequestProto);
            Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> result = asStream(responseIterator);
            log.info("Recommendations get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending UserPredictionsRequestProto: userId {}, size {}", userId, size, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> getSimilarEvent(
            SimilarEventsRequestProtoOuterClass.SimilarEventsRequestProto similarEventsRequestProto) {
        try {
            log.info("AnalyzerClient. Getting similarEvents: {}", similarEventsRequestProto);
            Iterator<RecommendedEventProtoOuterClass.RecommendedEventProto> responseIterator =
                    analyzerStub.getSimilarEvents(similarEventsRequestProto);
            Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> result = asStream(responseIterator);
            log.info("SimilarEvents get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending similarEventsRequestProto: {}", similarEventsRequestProto, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> getInteractionsCount(
            List<Long> interactionsCountList) {
        try {
            log.info("AnalyzerClient. Getting InteractionsCount: {}", interactionsCountList);

            InteractionsCountRequestProtoOuterClass.InteractionsCountRequestProto.Builder builder =
                     InteractionsCountRequestProtoOuterClass.InteractionsCountRequestProto.newBuilder();

            interactionsCountList.forEach(builder::addEventId);

            Iterator<RecommendedEventProtoOuterClass.RecommendedEventProto> responseIterator =
                    analyzerStub.getInteractionsCount(builder.build());
            Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> result = asStream(responseIterator);
            log.info("InteractionsCount get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending InteractionsCountRequestProto: {}", interactionsCountList, e);
            return Stream.empty();
        }
    }

    private Stream<RecommendedEventProtoOuterClass.RecommendedEventProto> asStream(
            Iterator<RecommendedEventProtoOuterClass.RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

}
