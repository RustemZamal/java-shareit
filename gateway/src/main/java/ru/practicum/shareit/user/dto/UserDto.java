package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    private final String name;

    @NotBlank(message = "email cannot be empty or contain spaces.")
    @Email(message = "Incorrect email! Please enter a valid email.")
    private final String email;
}
