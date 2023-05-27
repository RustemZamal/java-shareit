package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;


    /**
     * Эндпоинт по добовлению предмета.
     * @param userId индефикатор пользователя
     * @param itemDto объект предмент, котрый добавлят пользватель.
     * @return Возвращает объект, котрый добавил пользватель.
     */
    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,  @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    /**
     * Эндпонит по соэданию комментария.
     * @param bookerId id пользавателя.
     * @param itemId объект товар, которому оставляется комментарий.
     * @param commentDto комментарий.
     * @return Возвращает объкт комментария.
     */
    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return itemService.addNewComment(bookerId, itemId, commentDto);
    }

    /**
     * Эндпоинт по обновлениб предмета.
     * @param userId индефикатор пользователя
     * @param itemId идентификатор предмета
     * @param itemDto предмент, который обновляет пользватель.
     * @return Возвращает объект, котрый обновил пользватель.
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    /**
     * Эндпоинт по поиску предмета по индефикатору.
     * @param itemId индефикатор предмета
     * @return Возвращает предмет по его идентификатору.
     */
    @GetMapping("/{itemId}")
    public ItemAllFieldsDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        return itemService.getItemById(itemId, ownerId);
    }

    /**
     * Эндпоинт по поиску предмета по тексту.
     * @param text текст запроса.
     * @return Возвращает предмет.
     */
    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        return itemService.getItemBySearch(text, userId, new OffsetPageRequest(from, size));
    }

    /**
     * Эндпоитн по поиску предметов, которые принадлежат пользователю.
     * @param userId идентификатор пользователя
     * @return Возвращает предмет.
     */
    @GetMapping
    public List<ItemAllFieldsDto> getItemByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemByUserId(userId, new OffsetPageRequest(from, size));
    }
}
