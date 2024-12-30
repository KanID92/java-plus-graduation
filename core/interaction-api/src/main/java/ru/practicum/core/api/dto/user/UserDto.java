package ru.practicum.core.api.dto.user;

public record UserDto(
        String email,
        int id,
        String name
) {
}
