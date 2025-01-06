package ru.practicum.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.core.api.client.EventServiceClient;
import ru.practicum.core.api.client.LocationServiceClient;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.api.enums.EventState;
import ru.practicum.core.api.exception.ConflictException;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.like.repository.LikeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralLikeService implements LikeService {

    private final LikeRepository likeRepository;
    private final EventServiceClient eventServiceClient;
    private final LocationServiceClient locationServiceClient;

    @Override
    public Map<Long, Long> getAllEventsLikesByIds(List<Long> eventIdList) {
        return likeRepository.getAllEventsLikesByIds(eventIdList);
    }

    @Override
    public Long getCountByEventId(Long eventId) {
        return likeRepository.getCountByEventId(eventId);
    }

    @Override
    public Long getCountByLocationId(Long locationId) {
        return likeRepository.getCountByLocationId(locationId);
    }

    @Override
    public Long addEventLike(Long eventId, Long userId) {
        EventFullDto eventFullDto = eventServiceClient.getById(eventId);
        if (eventFullDto.state() != EventState.PUBLISHED) {
            throw new ConflictException("Event with id " + eventId + " is not published. Can't add like event");
        }
        return likeRepository.addEventLike(userId, eventId);
    }

    @Override
    public Long deleteEventLike(Long eventId, Long userId) {
        Optional<Boolean> isEventLikedByUser =
                likeRepository.isEventLiked(eventId, userId);
        if (isEventLikedByUser.isPresent() && isEventLikedByUser.get().equals(true)) {
            return likeRepository.deleteEventLike(eventId, userId);
        } else {
            throw new NotFoundException("Like of user:" + userId + "for event: " + eventId + "not found");
        }
    }

    @Override
    public Long addLocationLike(Long locationId, Long userId) {
        LocationDto locationDto = locationServiceClient.getById(locationId);
        return likeRepository.addLocationLike(locationId, userId);
    }

    @Override
    public Long deleteLocationLike(Long locationId, Long userId) {
        Optional<Boolean> isLocationLikedByUser =
                likeRepository.isLocationLiked(locationId, userId);
        if (isLocationLikedByUser.isPresent() && isLocationLikedByUser.get().equals(true)) {
            return likeRepository.deleteEventLike(locationId, userId);
        } else {
            throw new NotFoundException("Like of user:" + userId + "for event: " + locationId + "not found");
        }
    }

    @Override
    public Map<Long, Long> getTopLikedLocationsIds(Integer count) {
        return likeRepository.getTopLikedLocationsIds(count);
    }

    @Override
    public Map<Long, Long> getTopLikedEventsIds(Integer count) {
        return likeRepository.getTopLikedEventsIds(count);
    }
}
