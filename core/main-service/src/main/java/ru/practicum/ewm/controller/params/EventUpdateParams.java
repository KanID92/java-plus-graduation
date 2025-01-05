package ru.practicum.ewm.controller.params;

import ru.practicum.core.api.dto.event.UpdateEventAdminRequest;
import ru.practicum.core.api.dto.event.UpdateEventUserRequest;

public record EventUpdateParams(
        Long userId,
        UpdateEventUserRequest updateEventUserRequest,
        UpdateEventAdminRequest updateEventAdminRequest
) {
}
