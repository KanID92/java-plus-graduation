package ru.yandex.practicum.stats.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events_similarities")
public class EventSimilarity {

    @Id
    @Column(name = "event_similarity_id")
    private Long eventSimilarityId;

    @Column(name = "event_A_id")
    private long eventAId;

    @Column(name = "event_B_id")
    private long eventBId;

    @Column(name = "score")
    private double score;

    @Column(name = "timestamp")
    private Instant timestamp;

}
