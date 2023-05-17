package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    @NotBlank(message = "The text comment should not be empty.")
    private String text;
    private String authorName;

    private LocalDateTime created;
}
