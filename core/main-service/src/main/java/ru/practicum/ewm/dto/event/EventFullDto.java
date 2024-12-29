package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.config.Constants;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record EventFullDto(

        String annotation,

        CategoryDto category,

        long confirmedRequests,

        @JsonFormat(pattern = Constants.JSON_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        LocalDateTime createdOn,

        String description,

        @JsonFormat(pattern = Constants.JSON_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        LocalDateTime eventDate,

        Long id,

        UserShortDto initiator,

        Location location,

        boolean paid,

        Integer participantLimit,

        @JsonFormat(pattern = Constants.JSON_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        LocalDateTime publishedOn,

        boolean requestModeration,

        EventState state,

        String title,

        long views,

        long likesCount


) {

}
