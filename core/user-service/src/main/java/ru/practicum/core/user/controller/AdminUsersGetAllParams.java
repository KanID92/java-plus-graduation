package ru.practicum.core.user.controller;

public record AdminUsersGetAllParams(
        Long[] ids,
        int from,
        int size
) {
}
