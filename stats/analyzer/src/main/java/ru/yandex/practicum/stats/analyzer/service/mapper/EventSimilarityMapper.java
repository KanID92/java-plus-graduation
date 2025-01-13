package ru.yandex.practicum.stats.analyzer.service.mapper;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.analyzer.entity.EventSimilarity;

public interface EventSimilarityMapper {

    EventSimilarity toEventSimilarity(EventSimilarityAvro eventSimilarityAvro);
}
