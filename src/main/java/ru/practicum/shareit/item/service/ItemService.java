package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemAllFieldsDto getItemById(Long itemId, Long ownerId);

    Item getItemByIdAllField(Long id);

    List<ItemAllFieldsDto> getItemByUserId(Long userId);

    List<ItemDto> getItemBySearch(String text);

    CommentDto addNewComment(Long bookerId, Long itemId, CommentDto commentDto);
}
