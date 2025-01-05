package ru.practicum.request.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.practicum.core.api.enums.RequestStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GeneralJdbcRequestRepository implements RequestJdbcRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds) {
        String sql = """
                  select EVENT_ID, COUNT(*) as EVENT_COUNT
                  from REQUESTS where event_id in (:eventIds) AND status = (:status)
                  GROUP BY EVENT_ID
                  """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, Map.of("eventIds", eventsIds, "status", status.name()));

        Map<Long, Long> eventRequestsWithStatus = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Long eventId = (Long) row.get("EVENT_ID");
            Long statusCount = (Long) row.get("EVENT_COUNT");
            eventRequestsWithStatus.put(eventId, statusCount);
        }
        return eventRequestsWithStatus;

    }
}
