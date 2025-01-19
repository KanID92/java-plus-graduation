package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.stats.recommendations.proto.RecommendationMessages;
import ru.yandex.practicum.grpc.stats.recommendations.proto.RecommendationsControllerGrpc;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    public Stream<RecommendationMessages.RecommendedEventProto> getRecommendedEventsForUser(
            long userId, int size) {
        try {
            log.info("AnalyzerClient. Getting recommendation. UserId: {}, size: {}", userId, size);
            RecommendationMessages.UserPredictionsRequestProto predictionsRequestProto =
                    RecommendationMessages.UserPredictionsRequestProto.newBuilder()
                            .setUserId(userId)
                            .setMaxResults(size)
                            .build();
            Iterator<RecommendationMessages.RecommendedEventProto> responseIterator =
                    analyzerStub.getRecommendationsForUser(predictionsRequestProto);
            Stream<RecommendationMessages.RecommendedEventProto> result = asStream(responseIterator);
            log.info("Recommendations get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending UserPredictionsRequestProto: userId {}, size {}", userId, size, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessages.RecommendedEventProto> getSimilarEvent(
            RecommendationMessages.SimilarEventsRequestProto similarEventsRequestProto) {
        try {
            log.info("AnalyzerClient. Getting similarEvents: {}", similarEventsRequestProto);
            Iterator<RecommendationMessages.RecommendedEventProto> responseIterator =
                    analyzerStub.getSimilarEvents(similarEventsRequestProto);
            Stream<RecommendationMessages.RecommendedEventProto> result = asStream(responseIterator);
            log.info("SimilarEvents get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending similarEventsRequestProto: {}", similarEventsRequestProto, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessages.RecommendedEventProto> getInteractionsCount(
            List<Long> interactionsCountList) {
        try {
            log.info("AnalyzerClient. Getting InteractionsCount: {}", interactionsCountList);

            RecommendationMessages.InteractionsCountRequestProto.Builder builder =
                     RecommendationMessages.InteractionsCountRequestProto.newBuilder();

            interactionsCountList.forEach(builder::addEventId);

            Iterator<RecommendationMessages.RecommendedEventProto> responseIterator =
                    analyzerStub.getInteractionsCount(builder.build());
            Stream<RecommendationMessages.RecommendedEventProto> result = asStream(responseIterator);
            log.info("InteractionsCount get: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error sending InteractionsCountRequestProto: {}", interactionsCountList, e);
            return Stream.empty();
        }
    }

    private Stream<RecommendationMessages.RecommendedEventProto> asStream(
            Iterator<RecommendationMessages.RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

}
