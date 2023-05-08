package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestByOwnerId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam (defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("{id}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable(value = "id") Long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
