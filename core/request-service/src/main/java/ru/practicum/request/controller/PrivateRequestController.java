package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.CollectorClient;
import ru.practicum.core.api.dto.request.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;
import ru.yandex.practicum.grpc.stats.actions.ActionTypeProto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;
    private final CollectorClient collectorClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(
            @PathVariable long userId,
            @RequestParam long eventId) {
        ParticipationRequestDto receivedRequestDto = requestService.create(userId, eventId);
        collectorClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
        return receivedRequestDto;
    }

    @GetMapping
    public List<ParticipationRequestDto> getOwnRequests(
            @PathVariable long userId) {
        return requestService.getAllOwnRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(
            @PathVariable long userId,
            @PathVariable long requestId) {
        return requestService.cancel(userId, requestId);
    }








}
