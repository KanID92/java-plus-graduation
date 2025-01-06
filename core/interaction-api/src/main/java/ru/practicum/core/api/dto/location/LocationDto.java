package ru.practicum.core.api.dto.location;

public record LocationDto(
        long id,
        Float lat,
        Float lon,
        Long likes
) {
}


