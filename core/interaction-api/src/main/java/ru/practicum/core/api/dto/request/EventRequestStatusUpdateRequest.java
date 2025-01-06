package ru.practicum.core.api.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.practicum.core.api.enums.RequestStatus;

import java.util.List;

public record EventRequestStatusUpdateRequest(

        List<Long> requestIds,

        @NotNull
        RequestStatus status

) {
}
