package ru.practicum.core.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.location.entity.Location;


@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto locationToLocationDto(Location location);

    Location locationDtoToLocation(LocationDto locationDto);
}
