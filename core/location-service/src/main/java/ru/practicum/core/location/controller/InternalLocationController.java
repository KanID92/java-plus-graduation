package ru.practicum.core.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.location.service.LocationService;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/locations")
public class InternalLocationController {
    private final LocationService locationService;

    @GetMapping("/{locationId}")
    public LocationDto getById(@PathVariable long locationId) {
        log.info("==> GET /internal/locations/{locationId} Getting location with id: {}", locationId);
        LocationDto location = locationService.getById(locationId);
        log.info("<== GET /internal/locations/{locationId} Returning location with id: {}", location);
        return location;
    }

    @GetMapping("/all")
    public Map<Long, LocationDto> getAllById(@RequestParam List<Long> locationIds) {
        log.info("==> GET /internal/locations/ Getting Locations with ids: {}", locationIds);
        Map<Long, LocationDto> locations = locationService.getAllById(locationIds);
        log.info("<== GET /internal/locations/ Returning locations: {}", locations);
        return locations;
    }

}
