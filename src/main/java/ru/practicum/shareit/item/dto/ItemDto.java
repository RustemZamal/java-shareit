package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым!")
    private String name;

    @NotBlank(message = "Описание не может быть пустым!")
    private String description;

    @NotNull(message = "Статус о том дуоступен ли товар или нет не должен быть пустым!")
    private Boolean available;

    private Long requestId;
}
