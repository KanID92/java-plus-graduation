package ru.practicum.core.user.service;

import ru.practicum.core.api.dto.user.UserCreateDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.user.controller.AdminUsersGetAllParams;

import java.util.List;

public interface UserService {

    UserDto add(UserCreateDto userCreateDto);

    List<UserDto> getAll(AdminUsersGetAllParams adminUsersGetAllParams);

    void delete(long userId);

    void checkExistence(long userId);

}
