package ru.practicum.request.service;


import ru.practicum.core.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.core.api.dto.request.ParticipationRequestDto;
import ru.practicum.core.api.enums.RequestStatus;
import ru.practicum.request.controller.PrivateUpdateRequestParams;

import java.util.List;
import java.util.Map;

public interface RequestService {

    ParticipationRequestDto create(long userId, long eventId);

    List<ParticipationRequestDto> getAllOwnRequests(long userId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params);

    long countByStatusAndEventId(RequestStatus status, long eventId);

    Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds);

}
