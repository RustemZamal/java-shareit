package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private  final UserService userService;

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        validateItem(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);

        return itemRepository.saveItem(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findItemById(itemId);

        validateItem(userId);
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException(
                    String.format("Вещь с ID=%d не принадлежит пользователю с ID=%d", itemId, userId));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.updateItem(item);
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemRepository.findItemById(id));
    }

    @Override
    public List<Item> getItemByUserId(Long userId) {
        return itemRepository.findItemByUserId(userId);
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findAllItems()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Long userId) {
        if (userId == null) {
            throw new InvalidDataException("id пользователя не указан.");
        }

        if (userService.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с ID=%d не найден!", userId));
        }
    }
}
