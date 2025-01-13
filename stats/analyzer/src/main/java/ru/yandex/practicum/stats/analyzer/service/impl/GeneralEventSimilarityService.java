package ru.yandex.practicum.stats.analyzer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.stats.analyzer.entity.EventSimilarity;
import ru.yandex.practicum.stats.analyzer.repository.EventSimilarityRepository;
import ru.yandex.practicum.stats.analyzer.service.EventSimilarityService;
import ru.yandex.practicum.stats.analyzer.service.mapper.EventSimilarityMapper;

@Service
@RequiredArgsConstructor
public class GeneralEventSimilarityService implements EventSimilarityService {

    private final EventSimilarityRepository eventSimilarityRepository;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Override
    public EventSimilarity save(EventSimilarityAvro eventSimilarityAvro) {
        return eventSimilarityRepository.save(
                eventSimilarityMapper.toEventSimilarity(eventSimilarityAvro));
    }

}
