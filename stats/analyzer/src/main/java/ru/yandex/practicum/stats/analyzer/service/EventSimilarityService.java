package ru.yandex.practicum.stats.analyzer.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.analyzer.entity.EventSimilarity;

public interface EventSimilarityService {

    EventSimilarity save(EventSimilarityAvro eventSimilarityAvro);

}
