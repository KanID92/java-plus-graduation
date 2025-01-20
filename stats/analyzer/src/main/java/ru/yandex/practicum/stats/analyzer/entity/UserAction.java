package ru.yandex.practicum.stats.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users_actions")
public class UserAction {

    @Id
    @Column(name = "user_action_id")
    private long userActionId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "event_id")
    private long eventId;

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "timestamp")
    private Instant timestamp;

}
