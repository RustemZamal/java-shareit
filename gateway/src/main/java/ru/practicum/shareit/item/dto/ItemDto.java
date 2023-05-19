package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    @NotBlank(message = "The name cannot be empty!")
    private String name;

    @NotBlank(message = "The description cannot be empty!")
    private String description;

    @NotNull(message = "The status of whether the product is available or not should not be empty!")
    private Boolean available;

    private Long requestId;
}
