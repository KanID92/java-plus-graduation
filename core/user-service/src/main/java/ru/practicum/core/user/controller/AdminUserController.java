package ru.practicum.core.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.user.UserCreateDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.add(userCreateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getAll(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        AdminUsersGetAllParams adminUsersGetAllParams = new AdminUsersGetAllParams(ids, from, size);
        return userService.getAll(adminUsersGetAllParams);
    }

}

