package ru.practicum.core.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.core.api.dto.user.UserCreateDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.api.dto.user.UserShortDto;
import ru.practicum.core.user.entity.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User userCreateDtoToUser(UserCreateDto userCreateDto);

    UserDto userToUserDto(User user);

    UserShortDto userToUserShotDto(User user);

}
