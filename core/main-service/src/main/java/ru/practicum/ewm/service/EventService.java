package ru.practicum.ewm.service;

import ru.practicum.HitDto;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.event.EventShortDto;
import ru.practicum.core.api.dto.event.NewEventDto;
import ru.practicum.ewm.controller.params.EventGetByIdParams;
import ru.practicum.ewm.controller.params.EventUpdateParams;
import ru.practicum.ewm.controller.params.search.EventSearchParams;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getById(EventGetByIdParams params, HitDto hitDto);

    EventFullDto update(long eventId, EventUpdateParams updateParams);

    List<EventFullDto> getAllByAdmin(EventSearchParams searchParams);

    List<EventShortDto> getAllByInitiator(EventSearchParams searchParams);

    List<EventShortDto> getAllByPublic(EventSearchParams searchParams, HitDto hitDto);

    List<EventShortDto> getTopEvent(Integer count, HitDto hitDto);

    List<EventShortDto> getTopViewEvent(Integer count, HitDto hitDto);

    EventFullDto getByIdInternal(long eventId);
}
