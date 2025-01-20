package ru.practicum.core.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.location.service.LocationService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/locations")
public class InternalLocationController {
    private final LocationService locationService;

    @GetMapping("/{locationId}")
    public LocationDto getById(@PathVariable long locationId) {
        return locationService.getById(locationId);
    }

    @GetMapping("/all")
    public Map<Long, LocationDto> getAllById(@RequestParam List<Long> locationIds) {
        return locationService.getAllById(locationIds);
    }

}
