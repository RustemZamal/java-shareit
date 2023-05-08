package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Long ownerId = 1L;

    private final Long userId = 2L;

    private static UserDto userDto;

    private static User user;
    private static ItemRequest request;

    private static Item item;

    private static BookingSavingDto bookingSaving;

    private static BookingAllFieldsDto bookingAllFields;

    private static ItemDto itemDto;

    private static Booking  booking;

    @BeforeAll
    static void setUp() {

        userDto = new UserDto(1L, "name","jane.doe@example.org");

        user = new User(1L, "name","jane.doe@example.org");

        request = new ItemRequest();
        request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(user);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setId(1L);
        item.setName("Аккумуляторная дрель");
        item.setOwner(user);
        item.setRequest(request);

        itemDto = new ItemDto(
                1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                request.getId());

        bookingSaving = new BookingSavingDto(
                null,
                LocalDateTime.of(2022, 5,7,12,0),
                LocalDateTime.of(2022,5,9,10,0),
                item.getId(),
                user.getId(),
                null);

        bookingAllFields = new BookingAllFieldsDto(
                1L,
                LocalDateTime.of(2022, 5,7,12,0),
                LocalDateTime.of(2022,5,9,10,0),
                "APPROVED",
                userDto,
                itemDto);

        booking = new Booking(
                1L,
                LocalDateTime.of(2022, 5,7,12,0),
                LocalDateTime.of(2022,5,9,10,0),
                item,
                user,
                BookingStatus.APPROVED);
    }

    /**
     * Method under test: {@link BookingService#saveBooking(Long, BookingSavingDto)}
     */
    @Test
    void saveBooking() {
        when(itemService.getItemByIdAllField(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingAllFieldsDto actualBooking = bookingService.saveBooking(userId, bookingSaving);

        assertEquals(bookingAllFields, actualBooking);

    }

    /**
     * Method under test: {@link BookingService#approve(Long, Long, boolean)}
     */
    @Test
    void approve() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingAllFieldsDto actualBooking = bookingService.approve(ownerId, 1L, true);

        assertEquals(bookingAllFields, actualBooking);
    }


    /**
     * Method under test: {@link BookingService#getBookingById(Long, Long)}
     */
    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingAllFieldsDto actualBooking = bookingService.getBookingById(ownerId, 1L);

        assertEquals(bookingAllFields, actualBooking);
    }


    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingStateAll_thenReturnBookingByState() {
        when(bookingRepository.
                findByBookerIdOrderByStartDesc(anyLong(), any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBookings = bookingService
                .getBookingsByBookerId(ownerId, "ALL", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBookings);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingStateFUTURE_thenReturnBookingByState() {
        when(bookingRepository.
                findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBookings = bookingService
                .getBookingsByBookerId(ownerId, "FUTURE", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBookings);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingStateCURRENT_thenReturnBookingByState() {
        when(bookingRepository.
                findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBookings = bookingService
                .getBookingsByBookerId(ownerId, "CURRENT", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBookings);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingStatePAST_thenReturnBookingByState() {
        when(bookingRepository.
                findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBookings = bookingService
                .getBookingsByBookerId(ownerId, "PAST", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBookings);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingHasStatus_thenReturnBookingByStatus() {
        when(bookingRepository.
                findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBookings = bookingService
                .getBookingsByBookerId(ownerId, "APPROVED", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBookings);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByBookerId(Long, String, Pageable)}
     */
    @Test
    void getBookingsByBookerId_whenBookingStateUnsupported_thenReturnInvalidDataException() {
        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> bookingService.getBookingsByBookerId(ownerId, "UNSUPPORTED", new OffsetPageRequest(0, 2)));
        assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());

    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingStateALL_thenReturnBookingByState() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(),any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBooking = bookingService
                .getBookingsByOwner(ownerId, "ALL", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBooking);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingStateFUTURE_thenReturnBookingByState() {
        when(bookingRepository
                .findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBooking = bookingService
                .getBookingsByOwner(ownerId, "FUTURE", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBooking);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingStatePAST_thenReturnBookingByState() {
        when(bookingRepository
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBooking = bookingService
                .getBookingsByOwner(ownerId, "PAST", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBooking);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingStateCURRENT_thenReturnBookingByState() {
        when(bookingRepository
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBooking = bookingService
                .getBookingsByOwner(ownerId, "CURRENT", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBooking);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingHasStatus_thenReturnBookingByStatus() {
        when(bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any(OffsetPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingAllFieldsDto> actualBooking = bookingService
                .getBookingsByOwner(ownerId, "APPROVED", new OffsetPageRequest(0, 2));

        assertEquals(List.of(bookingAllFields), actualBooking);
    }

    /**
     * Method under test: {@link BookingService#getBookingsByOwner(Long, String, Pageable)}
     */
    @Test
    void getBookingsByOwner_whenBookingStateUnsupported_thenReturnInvalidDataException() {

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> bookingService.getBookingsByOwner(ownerId, "UNSUPPORTED", new OffsetPageRequest(0, 2)));
        assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());

    }
}