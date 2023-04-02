package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;


    /**
     * Эндпоинт по добовлению предмета.
     * @param userId индефикатор пользователя
     * @param itemDto объект предмент, котрый добавлят пользватель.
     * @return Возвращает объект, котрый добавил пользватель.
     */
    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,  @RequestBody @Valid ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    /**
     * Эндпоинт по обновлениб предмета.
     * @param userId индефикатор пользователя
     * @param itemId идентификатор предмета
     * @param itemDto предмент, который обновляет пользватель.
     * @return Возвращает объект, котрый обновил пользватель.
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
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
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    /**
     * Эндпоинт по поиску предмета по тексту.
     * @param text текст запроса.
     * @return Возвращает предмет.
     */
    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        return itemService.getItemBySearch(text);
    }

    /**
     * Эндпоитн по поиску предметов, которые принадлежат пользователю.
     * @param userId идентификатор пользователя
     * @return Возвращает предмет.
     */
    @GetMapping
    public List<ItemDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemByUserId(userId);
    }
}
