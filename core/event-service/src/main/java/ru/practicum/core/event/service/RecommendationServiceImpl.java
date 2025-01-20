package ru.practicum.core.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.core.api.dto.recommendation.RecommendationDto;
import ru.yandex.practicum.grpc.stats.recommendations.proto.RecommendationMessages;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final AnalyzerClient analyzerClient;

    @Override
    public List<RecommendationDto> getRecommendations(long userId, int size) {

        log.info("call analyzerClient.getRecommendedEventForUser: userId = {}, size {}", userId, size);
        List<RecommendationMessages.RecommendedEventProto> recommendations =
                analyzerClient.getRecommendedEventsForUser(userId, size).toList();
        log.info("analyzerClient.getRecommendedEventForUser  finished: userId = {}, size {}", userId, size);
        log.debug("result {{}}", recommendations);
        List<RecommendationDto> recommendationDtos = new ArrayList<>();
        recommendations.forEach(recommendationProto -> recommendationDtos.add(
                new RecommendationDto(recommendationProto.getEventId(), recommendationProto.getScore())));
        return recommendationDtos;
    }
}
