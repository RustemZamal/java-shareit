package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item saveItem(Item item);

    Item updateItem(Item item);

    Item findItemById(Long id);

    List<Item> findItemByUserId(Long userId);

    List<Item> findAllItems();
}
