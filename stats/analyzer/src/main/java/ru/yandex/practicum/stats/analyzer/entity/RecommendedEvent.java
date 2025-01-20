package ru.yandex.practicum.stats.analyzer.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecommendedEvent {

    private long eventId;
    private double score;

}
