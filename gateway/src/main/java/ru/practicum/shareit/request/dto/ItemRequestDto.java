package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class ItemRequestDto {

    @NotBlank(message = "description cannot be empty or null.")
    private String description;
}