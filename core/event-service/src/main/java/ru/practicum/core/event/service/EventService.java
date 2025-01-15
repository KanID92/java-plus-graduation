package ru.practicum.core.event.service;

import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.event.EventShortDto;
import ru.practicum.core.api.dto.event.NewEventDto;
import ru.practicum.core.event.controller.params.EventGetByIdParams;
import ru.practicum.core.event.controller.params.EventUpdateParams;
import ru.practicum.core.event.controller.params.search.EventSearchParams;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getById(EventGetByIdParams params);

    EventFullDto update(long eventId, EventUpdateParams updateParams);

    List<EventFullDto> getAllByAdmin(EventSearchParams searchParams);

    List<EventShortDto> getAllByInitiator(EventSearchParams searchParams);

    List<EventShortDto> getAllByPublic(EventSearchParams searchParams);

    List<EventShortDto> getTopEvent(Integer count);

    EventFullDto getByIdInternal(long eventId);

    Long addEventLike(long userId, long eventId);
}
