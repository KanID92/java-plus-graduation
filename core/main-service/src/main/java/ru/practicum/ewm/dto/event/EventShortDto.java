package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.config.Constants;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record EventShortDto(

        String annotation,

        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = Constants.JSON_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        LocalDateTime createOn,

        String description,

        @JsonFormat(pattern = Constants.JSON_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        LocalDateTime eventDate,

        Long id,

        UserShortDto initiator,

        Location location,

        boolean paid,

        String title,

        Long views,

        Long likesCount

) {
}
