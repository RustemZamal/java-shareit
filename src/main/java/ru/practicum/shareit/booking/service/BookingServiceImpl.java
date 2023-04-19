package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Override
    @Transactional
    public BookingAllFieldsDto saveBooking(Long userId, BookingSavingDto bookingSavingDto) {
        Item item = itemService.getItemByIdAllField(bookingSavingDto.getItemId());
        User booker = UserMapper.mapToUser(userService.getUserById(userId));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new InvalidDataException(
                    String.format("The item with ID=%d cannot be booked because it is not available!", item.getId()));
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("The item with ID=%d cannot be booked by its own owner!", item.getId()));
        }

        if (bookingSavingDto.getEnd().isBefore(bookingSavingDto.getStart())) {
            throw new InvalidDataException("The booking end date cannot be before the booking start date.");
        }

        if (bookingSavingDto.getEnd().isEqual(bookingSavingDto.getStart())) {
            throw new InvalidDataException("The booking end date and the booking start date cannot be the same.");
        }

        Booking booking = BookingMapper.mapToBooking(bookingSavingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.mapToBookingAllFieldsDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingAllFieldsDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Booking with ID=%d cannot be found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only the item owner can approve or reject the rental request.");
        }

        if ((!booking.getStatus().equals(BookingStatus.WAITING))) {
            throw new InvalidDataException("Only status [WAITING] can be updated");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToBookingAllFieldsDto(bookingRepository.save(booking));
    }

    @Override
    public BookingAllFieldsDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Booking with ID=%d cannot be found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("The item should belong to the item owner or the booking author.");
        }

        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByBookerId(Long bookerId, String state) {
        userService.getUserById(bookerId);

        if (state == null || BookingState.ALL.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(bookingRepository.findByBookerIdOrderByStartDesc(bookerId));
        }

        if (BookingState.FUTURE.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                                    bookerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (BookingState.PAST.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                            bookerId, LocalDateTime.now()));
        }

        if (BookingState.CURRENT.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            bookerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (Arrays.stream(BookingStatus.values()).anyMatch(status -> status.name().equals(state))) {
            return BookingMapper.mapToBookingAllFieldsDto(bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.valueOf(state)));
        } else {
            throw new InvalidDataException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByOwner(Long ownerId, String state) {
        userService.getUserById(ownerId);
        if (state == null || BookingState.ALL.name().equals(state)) {
            List<Booking> booking = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            return BookingMapper.mapToBookingAllFieldsDto(booking);
        }

        if (BookingState.FUTURE.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                            ownerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (BookingState.PAST.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                            ownerId, LocalDateTime.now()));
        }

        if (BookingState.CURRENT.name().equals(state)) {
            return BookingMapper.mapToBookingAllFieldsDto(
                    bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            ownerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (Arrays.stream(BookingStatus.values()).anyMatch(status -> status.name().equals(state))) {
            return BookingMapper.mapToBookingAllFieldsDto(bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.valueOf(state)));
        } else {
            throw new InvalidDataException(String.format("Unknown state: %s", state));
        }
    }
}
