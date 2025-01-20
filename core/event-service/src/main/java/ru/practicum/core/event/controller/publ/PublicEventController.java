package ru.practicum.core.event.controller.publ;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.CollectorClient;
import ru.practicum.core.api.constant.Constants;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.event.EventShortDto;
import ru.practicum.core.api.dto.recommendation.RecommendationDto;
import ru.practicum.core.api.enums.EventState;
import ru.practicum.core.api.exception.IncorrectValueException;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.event.controller.params.EventGetByIdParams;
import ru.practicum.core.event.controller.params.search.EventSearchParams;
import ru.practicum.core.event.controller.params.search.PublicSearchParams;
import ru.practicum.core.event.service.EventService;
import ru.practicum.core.event.service.RecommendationService;
import ru.yandex.practicum.grpc.stats.actions.ActionTypeProto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;
    private final CollectorClient collectorClient;
    private final RecommendationService recommendationService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.JSON_TIME_FORMAT);

    @GetMapping("/recommendations")
    public List<RecommendationDto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID") long userId, @RequestParam(name = "size", defaultValue = "10") int size) {
        return recommendationService.getRecommendations(userId, size);
    }

    @PutMapping("/{eventId}/like")
    public Long addEventLike(@RequestHeader("X-EWM-USER-ID") long userId, @PathVariable long eventId) {
        return eventService.addEventLike(eventId, userId);
    }

    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.JSON_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.JSON_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectValueException("rangeStart of event can't be after rangeEnd");
        }

        EventSearchParams eventSearchParams = new EventSearchParams();
        PublicSearchParams publicSearchParams = new PublicSearchParams();
        publicSearchParams.setText(text);
        publicSearchParams.setCategories(categories);
        publicSearchParams.setPaid(paid);

        publicSearchParams.setRangeStart(rangeStart);
        publicSearchParams.setRangeEnd(rangeEnd);

        eventSearchParams.setPublicSearchParams(publicSearchParams);
        eventSearchParams.setFrom(from);
        eventSearchParams.setSize(size);

        return eventService.getAllByPublic(eventSearchParams);
    }

    @GetMapping("/top")
    public List<EventShortDto> getTop(
            @RequestParam(required = false, defaultValue = "10") Integer count) {

        return eventService.getTopEvent(count);
    }

    @GetMapping("/{id}")
    public EventFullDto getById(
            @PathVariable Long id, @RequestHeader("X-EWM-USER-ID") long userId) {
        EventFullDto eventFullDto = eventService.getById(new EventGetByIdParams(null, id));
        if (eventFullDto.state() != EventState.PUBLISHED) {
            throw new NotFoundException("Нет опубликованных событий с id " + id);
        }
        collectorClient.sendUserAction(userId, id, ActionTypeProto.ACTION_VIEW);
        return eventFullDto;
    }
}
