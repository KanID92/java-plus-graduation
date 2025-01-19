package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.enums.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/requests")
public class InternalRequestController {

    private final RequestService requestService;

    @GetMapping("/count/{eventId}")
    public long countByStatusAndEventId(@RequestParam RequestStatus status, @PathVariable long eventId) {
        return requestService.countByStatusAndEventId(status, eventId);
    }

    @GetMapping("/count")
    public Map<Long, Long> countByStatusAndEventsIds(@RequestParam RequestStatus status, @RequestParam List<Long> eventsIds) {
        return requestService.countByStatusAndEventsIds(status, eventsIds);
    }

    @GetMapping("/{eventId}/{userId}/status")
    public boolean isUserHasConfirmedRequest(@PathVariable long eventId, @PathVariable long userId) {
        return requestService.existsByEventIdAndRequesterIdAndStatus(eventId, userId, RequestStatus.CONFIRMED);
    }


}
