package ru.practicum.core.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.core.api.dto.recommendation.RecommendationDto;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProtoOuterClass;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final AnalyzerClient analyzerClient;

    @Override
    public List<RecommendationDto> getRecommendations(long userId, int size) {

        List<RecommendedEventProtoOuterClass.RecommendedEventProto> recommendations =
                analyzerClient.getRecommendedEventsForUser(userId, size).toList();
        List<RecommendationDto> recommendationDtos = new ArrayList<>();
        recommendations.forEach(recommendationProto -> recommendationDtos.add(
                new RecommendationDto(recommendationProto.getEventId(), recommendationProto.getScore())));
        return recommendationDtos;
    }
}
