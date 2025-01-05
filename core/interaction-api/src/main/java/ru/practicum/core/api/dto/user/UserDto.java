package ru.practicum.core.api.dto.user;

public record UserDto(
        String email,
        Long id,
        String name
) {
}
