package ru.yandex.practicum.stats.aggregator.repository;

import java.util.List;

public interface EventSimilarityRepository {

    void putMinWeightSum(long eventA, long eventB, double sum);

    double getMinWeightSum(long eventA, long eventB);

    void putEventTotalSum(long event, double sum);

    double getEventTotalSum(long eventId);

    List<Long> getAllEventIds();
}
