package ru.practicum.request.repository;

import ru.practicum.core.api.enums.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestJdbcRepository {

    Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds);

}
