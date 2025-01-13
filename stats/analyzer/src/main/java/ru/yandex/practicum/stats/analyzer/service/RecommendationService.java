package ru.yandex.practicum.stats.analyzer.service;

import ru.yandex.practicum.stats.analyzer.entity.RecommendedEvent;

import java.util.List;

public interface RecommendationService {

    List<RecommendedEvent> getRecommendedEventsForUser(long userId, int maxValue);

    List<RecommendedEvent> getSimilarEvents(long eventId, long userId, long maxValue);

    List<RecommendedEvent> getInteractionsCount(List<Long> eventsIds);

}
