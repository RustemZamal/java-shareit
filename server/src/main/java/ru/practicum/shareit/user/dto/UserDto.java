package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public final class UserDto {

    private final Long id;

    private final String name;

    private final String email;
}
