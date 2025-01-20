package ru.yandex.practicum.stats.analyzer.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.analyzer.entity.EventSimilarity;

@Component
public class GeneralEventSimilarityMapper implements EventSimilarityMapper {

    @Override
    public EventSimilarity toEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .eventAId(eventSimilarityAvro.getEventA())
                .eventBId(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}
