package ru.practicum.core.api.dto.request;

import ru.practicum.core.api.enums.RequestStatus;

import java.time.LocalDateTime;

public record ParticipationRequestDto(

        LocalDateTime created,

        Long event,

        Long id,

        Long requester,

        RequestStatus status
) {
}
