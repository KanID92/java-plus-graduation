package ru.practicum.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.like.service.LikeService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/likes")
public class InternalLikeController {

    private final LikeService likeService;

    @GetMapping("/events/{eventId}")
    public Long getCountByEventId(@PathVariable Long eventId) {
        return likeService.getCountByEventId(eventId);
    }

    @GetMapping("/events")
    public Map<Long, Long> getAllEventsLikesByIds(@RequestParam List<Long> eventIdList) {
        return likeService.getAllEventsLikesByIds(eventIdList);
    }


    @GetMapping("/locations/{locationId}")
    public Long getCountByLocationId(@PathVariable Long locationId) {
        return likeService.getCountByLocationId(locationId);
    }

    @GetMapping("/locations/top")
    Map<Long, Long> getTopLikedLocationsIds(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return likeService.getTopLikedLocationsIds(count);
    }

    @GetMapping("/events/top")
    Map<Long, Long> getTopLikedEventsIds(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return likeService.getTopLikedEventsIds(count);
    }
}
