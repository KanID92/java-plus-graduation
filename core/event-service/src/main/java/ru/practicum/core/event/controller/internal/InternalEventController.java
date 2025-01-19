package ru.practicum.core.event.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.event.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/events")
public class InternalEventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long eventId) {
        return eventService.getByIdInternal(eventId);
    }

}
