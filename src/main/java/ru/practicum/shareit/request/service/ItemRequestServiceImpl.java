package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = checkPermission(userId);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, user);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public List<ItemRequestDto> getItemRequestByOwnerId(Long userId) {
        checkPermission(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<Item>> items = itemRepository.findAllByRequestIdIn(requestIds)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.mapToItemRequestDto(
                        itemRequest, ItemMapper.mapToItemDto(items.get(itemRequest.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        checkPermission(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdNot(userId, new OffsetPageRequest(from, size, Sort.by("created").descending()));
        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<ItemDto>> items = ItemMapper.mapToItemDto(itemRepository.findAllByRequestIdIn(requestIds))
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.mapToItemRequestDto(
                        itemRequest,
                        items.getOrDefault(itemRequest.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        checkPermission(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("Request with ID=%d was not found.", requestId)));
        List<ItemDto> items = ItemMapper.mapToItemDto(itemRepository.findAllByRequestId(itemRequest.getId()));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    private User checkPermission(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("You don't have permission to perform this operation."));
    }
}
