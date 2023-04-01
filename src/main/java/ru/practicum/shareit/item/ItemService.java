package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long id);

    List<Item> getItemByUserId(Long userId);

    List<ItemDto> getItemBySearch(String text);

}
