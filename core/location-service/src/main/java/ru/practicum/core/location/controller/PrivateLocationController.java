package ru.practicum.core.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.location.service.LocationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/locations")
public class PrivateLocationController {

    private final LocationService locationService;

    @GetMapping("/top")
    public List<LocationDto> getTop(
            @PathVariable long userId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {

        return locationService.getTop(userId, count);
    }

    @PostMapping
    public LocationDto create(@PathVariable long userId,
                              @RequestBody LocationDto location) {
        return locationService.create(location);
    }








}



