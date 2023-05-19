package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public final class ItemDto {

    private final Long id;

    @NotBlank(message = "The name cannot be empty!")
    private final String name;

    @NotBlank(message = "The description cannot be empty!")
    private final String description;

    @NotNull(message = "The status of whether the product is available or not should not be empty!")
    private final Boolean available;

    private final Long requestId;
}