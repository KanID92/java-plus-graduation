package ru.practicum.like.repository;


import java.util.List;
import java.util.Map;

public interface LikeRepository {

    //EVENTS
    Long addEventLike(long userId, long eventId);

    Long deleteEventLike(long userId, long eventId);

    Long getCountByEventId(long eventId);

    Long getCountByLocationId(long locationId);

    Map<Long, Long> getAllEventsLikesByIds(List<Long> eventIds);

    Map<Long, Long> getTopLikedEventsIds(Integer count);

    //LOCATIONS
    Long addLocationLike(Long locationId, Long userId);

    Long deleteLocationLike(Long locationId, Long userId);

    Map<Long, Long> getTopLikedLocationsIds(Integer count);



}