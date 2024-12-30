package ru.practicum.core.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.user.UserCreateDto;
import ru.practicum.core.api.dto.user.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserServiceClient {

    @GetMapping("/{userId}/check}")
    void checkExistence(@PathVariable("userId") long userId);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") long userId);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto add(@RequestBody @Valid UserCreateDto userCreateDto);

    @GetMapping
    List<UserDto> getAll(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    );

}