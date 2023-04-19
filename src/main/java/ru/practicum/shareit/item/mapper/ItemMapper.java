package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemAllFieldsDto mapToItemAllFieldsDto(Item item,
                                                         Booking next,
                                                         Booking last,
                                                         List<CommentDto> comments) {
        return new ItemAllFieldsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                last != null ? new BookingDto(last.getId(), last.getBooker().getId()) : null,
                next != null ? new BookingDto(next.getId(), next.getBooker().getId()) : null,
                comments != null ? comments : List.of());
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
