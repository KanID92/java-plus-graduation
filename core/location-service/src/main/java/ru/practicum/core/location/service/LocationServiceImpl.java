package ru.practicum.core.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.core.api.client.UserServiceClient;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.location.entity.Location;
import ru.practicum.core.location.mapper.LocationMapper;
import ru.practicum.core.location.repository.LocationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final UserServiceClient userServiceClient;
    private final LocationMapper locationMapper;

    @Override
    public LocationDto addLike(long userId, long locationId) {
        userServiceClient.checkExistence(userId);

        locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        locationRepository.addLike(userId, locationId);
        return locationMapper.locationToLocationDto(locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found")));
    }

    @Override
    public void deleteLike(long userId, long locationId) {
        userServiceClient.checkExistence(userId);

        locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        if (locationRepository.checkLikeExisting(userId, locationId)) {
            locationRepository.deleteLike(userId, locationId);
        } else {
            throw new NotFoundException("Like for Location: " + locationId + " by user: " + userId + " not exist");
        }

    }

    @Override
    public List<LocationDto> getTop(long userId, Integer count) {
        userServiceClient.checkExistence(userId);

        List<Location> locationTopList = locationRepository.findTop(count);

        for (Location location : locationTopList) {
            location.setLikes(locationRepository.countLikesByLocationId(location.getId()));
        }

        return locationTopList.stream()
                .map(locationMapper::locationToLocationDto)
                .toList();
    }

}
