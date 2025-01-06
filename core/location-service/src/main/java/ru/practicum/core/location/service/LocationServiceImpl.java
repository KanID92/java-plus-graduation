package ru.practicum.core.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.core.api.client.LikeServiceClient;
import ru.practicum.core.api.client.UserServiceClient;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.location.entity.Location;
import ru.practicum.core.location.mapper.LocationMapper;
import ru.practicum.core.location.repository.LocationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final UserServiceClient userServiceClient;
    private final LocationMapper locationMapper;
    private final LikeServiceClient likeServiceClient;


    @Override
    public List<LocationDto> getTop(long userId, Integer count) {
        userServiceClient.checkExistence(userId);
        Map<Long, Long> topLocationIds = likeServiceClient.getTopLikedLocationsIds(count);

        List<Location> locationTopList = locationRepository.findAllById(topLocationIds.keySet());

        for (Location location : locationTopList) {
            location.setLikes(topLocationIds.get(location.getId()));
        }

        return locationTopList.stream()
                .map(locationMapper::locationToLocationDto)
                .toList();
    }

    @Override
    public LocationDto create(LocationDto location) {
        Location savedLocation = locationRepository.save(
                locationMapper.locationDtoToLocation(location));
        return locationMapper.locationToLocationDto(savedLocation);
    }

    @Override
    public LocationDto getById(long locationId) {
        Optional<Location> locationOptional = locationRepository.findById(locationId);
        if (locationOptional.isPresent()) {
            return locationMapper.locationToLocationDto(locationOptional.get());
        } else {
            throw new NotFoundException("location with id " + locationId + " not found");
        }
    }


    @Override
    public Map<Long, LocationDto> getAllById(List<Long> locationIds) {
        List<Location> locationList = locationRepository.findAllById(locationIds);
        Map<Long, LocationDto> locationDtoMap = new HashMap<>();
        for (Location location : locationList) {
            locationDtoMap.put(location.getId(), locationMapper.locationToLocationDto(location));
        }
        return locationDtoMap;
    }

}
