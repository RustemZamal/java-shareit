package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "description cannot be empty or null.")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
