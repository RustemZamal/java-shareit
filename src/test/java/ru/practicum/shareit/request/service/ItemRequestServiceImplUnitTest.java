package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final Long userId = 1L;

    private ItemRequestDto requestDto;

    private ItemRequest request;

    private ItemDto itemDto;

    private Item item;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User(1L, "Mike", "mike@gmail.com");

        itemDto = new ItemDto(
                1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                2L);

        requestDto = new ItemRequestDto(
                1L,
                "Хотел бы воспользоваться Дрелью",
                LocalDateTime.of(2022, 5, 7, 23, 10, 30),
                List.of());

        request = new ItemRequest(
                1L,
                "Хотел бы воспользоваться Дрелью",
                user,
                LocalDateTime.of(2022, 5, 7, 23, 10, 30));

        item = new Item(
                1L,
                user,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                request);
    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#createItemRequest(Long, ItemRequestDto)}
     */
    @Test
    void createItemRequest() {
        requestDto.setCreated(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(request);

        ItemRequestDto actualRequest = requestService.createItemRequest(userId, requestDto);

        assertNotNull(actualRequest.getCreated());
        assertEquals(requestDto.getId(), actualRequest.getId());

    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#createItemRequest(Long, ItemRequestDto)}
     */
    @Test
    void createItemRequest_whenUserNotFound_thenNotSaveItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.createItemRequest(userId, requestDto));
        assertEquals("You don't have permission to perform this operation.", exception.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#getItemRequestByOwnerId(Long)}
     */
    @Test
    void getItemRequestByOwnerId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> actualRequests = requestService.getItemRequestByOwnerId(userId);

        assertEquals(List.of(requestDto), actualRequests);
    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#getAllItemRequests(Integer, Integer, Long)}
     */
    @Test
    void getAllItemRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any(OffsetPageRequest.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> actualRequests = requestService.getAllItemRequests(0, 2, userId);

        assertEquals(List.of(requestDto), actualRequests);
    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#getItemRequestById(Long, Long)}
     */
    @Test
    void getItemRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto actualResult = requestService.getItemRequestById(request.getId(), userId);

        assertEquals(requestDto, actualResult);
    }

    /**
     * Method under test: {@link ItemRequestServiceImpl#getItemRequestById(Long, Long)}
     */
    @Test
    void getItemRequestById_whenNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.getItemRequestById(request.getId(), userId));
        assertEquals("Request with ID=1 was not found.", exception.getMessage());
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }
}