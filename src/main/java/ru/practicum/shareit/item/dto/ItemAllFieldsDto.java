package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemAllFieldsDto {

    private final Long id;

    private final String name;

    private final String description;

    private final Boolean available;

    private final BookingDto lastBooking;

    private final BookingDto nextBooking;

    private List<CommentDto> comments;

    @Data
    @AllArgsConstructor
    public static class BookingDto {

        private Long id;

        private Long bookerId;
    }
}
