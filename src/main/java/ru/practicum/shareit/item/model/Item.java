package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;

    private Long owner;

    @NotBlank(message = "Название не может быть пустым!")
    private String name;

    @NotBlank(message = "Описание не может быть пустым!")
    private String description;

    @NotNull(message = "Статус о том дуоступен ли товар или нет не должен быть пустым!")
    private Boolean available;

    private ItemRequest request;

}
