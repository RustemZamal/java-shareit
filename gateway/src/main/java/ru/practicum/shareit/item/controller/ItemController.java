package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    /**
     * Эндпоинт по добовлению предмета.
     *
     * @param userId  индефикатор пользователя
     * @param itemDto объект предмент, котрый добавлят пользватель.
     * @return Возвращает объект, котрый добавил пользватель.
     */
    @PostMapping
    public ResponseEntity<Object> addNewItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item={}, userid={}", itemDto, userId);
        return itemClient.addNewItem(userId, itemDto);
    }

    /**
     * Эндпонит по соэданию комментария.
     * @param bookerId id пользавателя.
     * @param itemId объект товар, которому оставляется комментарий.
     * @param commentDto комментарий.
     * @return Возвращает объкт комментария.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating comment={}, bookerId={}, itemId={}", commentDto, bookerId, itemId);
        return itemClient.addNewComment(bookerId, itemId, commentDto);
    }

    /**
     * Эндпоинт по обновлениб предмета.
     * @param userId индефикатор пользователя
     * @param itemId идентификатор предмета
     * @param itemDto предмент, который обновляет пользватель.
     * @return Возвращает объект, котрый обновил пользватель.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Update item={}, itemId{}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    /**
     * Эндпоинт по поиску предмета по индефикатору.
     * @param itemId индефикатор предмета
     * @return Возвращает предмет по его идентификатору.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        log.info("Get item by itemId={}, ownerId={}", itemId, ownerId);
        return itemClient.getItemById(itemId, ownerId);
    }

    /**
     * Эндпоинт по поиску предмета по тексту.
     * @param text текст запроса.
     * @return Возвращает предмет.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("Searching item={}, from{}, size{}, userId={}", text, from, size, userId);
        return itemClient.getItemBySearch(text, userId, from, size);
    }

    /**
     * Эндпоитн по поиску предметов, которые принадлежат пользователю.
     * @param userId идентификатор пользователя
     * @return Возвращает предмет.
     */
    @GetMapping
    public ResponseEntity<Object> getItemByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("Get item by userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemByUserId(userId, from, size);
    }
}
