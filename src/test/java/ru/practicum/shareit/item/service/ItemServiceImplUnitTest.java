package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    private final Long userId = 1L;

    private final Long itemId = 1L;

    private final UserDto userDto = new UserDto(1L, "Mike", "mike@gmail.com");

    private final User user = new User(1L, "Mike", "mike@gmail.com");

    private final ItemRequest itemRequest = new ItemRequest(
            2L,
            "Нужен трактор",
            user,
            LocalDateTime.of(2023, 5, 5, 17, 15, 30));

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            2L);

    private final Item item = new Item(
            1L,
            user,
            "Аккумуляторная дрель",
            "Аккумуляторная дрель + аккумулятор",
            true,
            itemRequest);

    private final Comment comment = new Comment(
            1L,
            "Add comment from user1",
            item,
            user,
            LocalDateTime.of(2023, 5, 5, 17, 15, 30));

    private final CommentDto commentDto = new CommentDto(
            1L,
            "Add comment from user1",
            "Mike",
            LocalDateTime.of(2023, 5, 5, 17, 11, 30));

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 5, 2, 17, 11, 30),
            LocalDateTime.of(2023, 5, 5, 8, 0, 10),
            item,
            user,
            BookingStatus.APPROVED);

    /**
     * Method under test: {@link ItemServiceImpl#addNewItem(Long, ItemDto)}
     */
    @Test
    void addNewItem() {
        Item item = new Item(1L,
                user, "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                itemRequest);
        when(itemRepository.save(any())).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(userDto);

        ItemDto actualItem = itemService.addNewItem(userId, itemDto);

        assertEquals(itemDto, actualItem);
        verify(itemRequestRepository, times(1)).findById(itemDto.getRequestId());
        verifyNoMoreInteractions(itemRepository);
    }

    /**
     * Method under test: {@link ItemServiceImpl#addNewItem(Long, ItemDto)}
     */
    @Test
    void addNewItem_whenUserNotFound_thenNotSaveItem_AndThrowNotFoundEx() {
        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                itemService.addNewItem(userId, itemDto
                ));

        verify(itemRepository, never()).save(item);
    }

    /**
     * Method under test: {@link ItemServiceImpl#updateItem(Long, Long, ItemDto)}
     */
    @Test
    void updateItem() {
        ItemDto newItem = new ItemDto(
                1L,
                "Новая дрель",
                "Аккумуляторная дрель",
                true,
                2L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItem = itemService.updateItem(userId, itemId, newItem);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(newItem.getName(), savedItem.getName());
        assertEquals(newItem.getDescription(), savedItem.getDescription());
    }

    /**
     * Method under test: {@link ItemServiceImpl#updateItem(Long, Long, ItemDto)}
     */
    @Test
    void updateItem_whenItemDonBelongToOwner_thenThrowNotFoundException() {
        ItemDto newItem = new ItemDto(
                1L,
                "Новая дрель",
                "Аккумуляторная дрель",
                true,
                2L);
        String message = "Вещь с ID=1 не принадлежит пользователю с ID=2";
        Long wrongUserId = 2L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(wrongUserId, itemId, newItem));
        assertEquals(message, exception.getMessage());
        verify(itemRepository, never()).save(itemArgumentCaptor.capture());
    }


    /**
     * Method under test: {@link ItemServiceImpl#getItemById(Long, Long)}
     */
    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemAllFieldsDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(item.getId(), actualItem.getId());
        verify(bookingRepository, times(1))
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, userId, BookingStatus.REJECTED);
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreated(itemId);
    }

    /**
     * Method under test: {@link ItemServiceImpl#getItemById(Long, Long)}
     */
    @Test
    void getItemById_whenItemNotFound_thenNotFoundException() {
        when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () ->
                itemService.getItemById(itemId, userId));
        verify(bookingRepository, never())
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, userId, BookingStatus.REJECTED);
        verify(commentRepository, never()).findAllByItemIdOrderByCreated(itemId);
    }

    @Test
    void getItemByIdAllField() {
    }

    /**
     * Method under test: {@link ItemServiceImpl#getItemByUserId(Long, Pageable)}
     */
    @Test
    void getItemByUserId() {
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(commentRepository.findByItemIdInOrderByItemId(anyList())).thenReturn(List.of(comment));
        when(bookingRepository
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(
                        anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<ItemAllFieldsDto> actualItems = itemService.getItemByUserId(userId, new OffsetPageRequest(0, 2));


        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
        verify(commentRepository, times(1)).findByItemIdInOrderByItemId(List.of(itemId));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED);

    }

    /**
     * Method under test: {@link ItemServiceImpl#getItemBySearch(String, Pageable)}
     */
    @Test
    void getItemBySearch() {
        when(itemRepository.searchByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> actualItems = itemService.getItemBySearch("дрель", new OffsetPageRequest(0, 2));

        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
        assertEquals(item.getName(), actualItems.get(0).getName());
    }

    /**
     * Method under test: {@link ItemServiceImpl#addNewComment(Long, Long, CommentDto)}
     */
    @Test
    void addNewComment() {
        when(bookingRepository.findDistinctBookingByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actualComment = itemService.addNewComment(userId, itemId, commentDto);

        assertEquals(comment.getId(), actualComment.getId());
        assertEquals(comment.getText(), actualComment.getText());
    }
}