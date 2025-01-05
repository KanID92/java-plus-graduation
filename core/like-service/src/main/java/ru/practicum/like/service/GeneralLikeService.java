package ru.practicum.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.like.repository.LikeRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneralLikeService implements LikeService {

    private final LikeRepository likeRepository;

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
        return likeRepository.addEventLike(userId, eventId);
    }

    @Override
    public Long deleteEventLike(Long eventId, Long userId) {
        return likeRepository.deleteEventLike(userId, eventId);
    }

    @Override
    public Long addLocationLike(Long locationId, Long userId) {
        return likeRepository.addLocationLike(locationId, userId);
    }

    @Override
    public Long deleteLocationLike(Long locationId, Long userId) {
        return likeRepository.deleteLocationLike(locationId, userId);
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
