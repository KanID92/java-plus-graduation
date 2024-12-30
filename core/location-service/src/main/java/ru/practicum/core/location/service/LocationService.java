package ru.practicum.core.location.service;

import ru.practicum.core.api.dto.location.LocationDto;

import java.util.List;

public interface LocationService {

    LocationDto addLike(long userId, long locationId);

    void deleteLike(long userId, long locationId);

    List<LocationDto> getTop(long userId, Integer count);
}
