package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        User user = UserMapper.mapToUser(userService.getUserById(userId));
        ItemRequest itemRequest = itemDto.getRequestId() != null ?
                requestRepository.findById(itemDto.getRequestId()).orElse(null) : null;
        item.setOwner(user);
        item.setRequest(itemRequest);

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID=%d was not found!", itemId)));

        if (!item.getOwner().getId().equals(userId)) {
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

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemAllFieldsDto getItemById(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Item with ID=%d was not found.", itemId)));

        List<Booking> bookings = bookingRepository
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, ownerId, BookingStatus.REJECTED);
        List<CommentDto> comments = CommentMapper.mapToCommentDto(commentRepository.findAllByItemIdOrderByCreated(itemId));

        return ItemMapper.mapToItemAllFieldsDto(item, getNextBooking(bookings), getLastBooking(bookings), comments);
 }

    @Override
    public Item getItemByIdAllField(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID=%d was not found!", id)));
    }

    @Override
    public List<ItemAllFieldsDto> getItemByUserId(Long userId, Pageable pageable) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, pageable);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> comments = commentRepository.findByItemIdInOrderByItemId(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        Map<Long, List<Booking>> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items
                .stream()
                .map(item -> ItemMapper.mapToItemAllFieldsDto(
                        item,
                        getNextBooking(bookings.getOrDefault(item.getId(), List.of())),
                        getLastBooking(bookings.getOrDefault(item.getId(), List.of())),
                        CommentMapper.mapToCommentDto(comments.get(item.getId()))
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, Long userId, Pageable pageable) {
        userService.getUserById(userId);
        return itemRepository.searchByText(text.toLowerCase(), pageable)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long bookerId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findDistinctBookingByBookerIdAndItemId(bookerId, itemId);

       if (bookings.isEmpty()) {
            throw new InvalidDataException(
                    String.format(
                            "A user with ID=%d cannot leave a comment because he/she didn't rent this item.", bookerId));
        }

       Booking booking = bookings
               .stream()
               .filter(b -> b.getStatus().name().equals("APPROVED"))
               .findAny()
               .orElseThrow(() -> new InvalidDataException(
                       String.format(
                               "The user with ID=%d cannot leave a comment, as he/she has not yet given the item", bookerId)));


        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new InvalidDataException("A comment can be left only after the end of the rent period.");
        }

        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setAuthor(booking.getBooker());
        comment.setItem(booking.getItem());

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        return bookings
                .stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(comparing(Booking::getEnd))
                .orElse(null);
    }
}
