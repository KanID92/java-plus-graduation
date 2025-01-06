package ru.practicum.core.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.location.service.LocationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/locations")
public class PrivateLocationController {

    private final LocationService locationService;

    @GetMapping("/top")
    public List<LocationDto> getTop(
            @PathVariable long userId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("==> GET /users/{userId}/locations/top");

        List<LocationDto> locationDtoList = locationService.getTop(userId, count);
        log.info("<== GET /users/{userId}/locations/top Returning top {} locations.", count);
        return locationDtoList;
    }

    @PostMapping
    public LocationDto create(@PathVariable long userId,
                              @RequestBody LocationDto location) {
        log.info("==> POST /users/{userId}/locations");
        LocationDto savedLocation = locationService.create(location);
        log.info("<== POST /users/{userId}/locations Location created.");
        return savedLocation;
    }








}



