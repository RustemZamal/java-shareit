package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestByOwnerId(Long userId);


    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}
