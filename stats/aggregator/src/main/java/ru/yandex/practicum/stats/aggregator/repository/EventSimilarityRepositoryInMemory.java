package ru.yandex.practicum.stats.aggregator.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventSimilarityRepositoryInMemory implements EventSimilarityRepository {

    private final Map<Long, Double> eventsTotalSums = new HashMap<>();

    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();

    public void putMinWeightSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSums
                .computeIfAbsent(first, k -> new HashMap<>())
                .put(second, sum);
    }

    public double getMinWeightSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }

    public void putEventTotalSum(long eventId, double sum) {
        eventsTotalSums.put(eventId, sum);
    }

    public double getEventTotalSum(long eventId) {
        return eventsTotalSums
                .getOrDefault(eventId, 0.0);
    }

    public List<Long> getAllEventIds() {
        return eventsTotalSums.keySet().stream().toList();
    }


}
