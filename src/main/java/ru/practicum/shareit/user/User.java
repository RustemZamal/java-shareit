package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    @NotBlank(message = "email не можеть быть пустым или содержать пробелы.")
    @Email(message = "Не корректнвй email! Пожалуйста, введите корректный email.")
    private String email;
}
