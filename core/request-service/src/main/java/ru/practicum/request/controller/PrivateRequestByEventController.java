package ru.practicum.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.core.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.core.api.dto.request.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateRequestByEventController {

    private final RequestService requestService;

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForOwnEvent(
            @PathVariable long userId,
            @PathVariable long eventId) {
        log.info("==> GET. /users/{userId}/events/{eventId}/requests " +
                "Getting requests for own event with id: {}, of user with id: {}", eventId, userId);

        List<ParticipationRequestDto> receivedRequestsDtoList
                = requestService.getAllForOwnEvent(userId, eventId);

        log.info("<== GET. /users/{userId}/events/{eventId}/requests " +
                "Returning requests for own event with id: {} of user with id: {}", eventId, userId);

        return receivedRequestsDtoList;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequestStatusDto) {

        log.info("==> PATCH. /users/{userId}/events/{eventId}/requests " +
                "Changing request status for own event with id: {} of user with id: {}", eventId, userId);
        log.info("EventRequestStatusUpdateRequest. Deserialized body: {}", updateRequestStatusDto);
        EventRequestStatusUpdateResult eventUpdateResult =
                requestService.updateStatus(new PrivateUpdateRequestParams(userId, eventId, updateRequestStatusDto));
        log.info("<== PATCH. /users/{userId}/events/{eventId}/requests " +
                "Changed request status for own event with id: {} of user with id: {}", eventId, userId);
        return eventUpdateResult;
    }

}
