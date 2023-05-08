package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired

    BookingRepository bookingRepository;

    @Autowired

    UserRepository userRepository;

    @Autowired

    ItemRequestRepository requestRepository;

    @Autowired

    ItemRepository itemRepository;

    private User owner;

    private User booker;

    private ItemRequest request;

    private Item item;

    private Booking bookingFuture;

    private Booking bookingPast;

    private Booking bookingCurrent;

    private final PageRequest page = PageRequest.of(0, 4);

    @BeforeEach
    void setUp() {
        owner = new User(null, "name","jane.doe@example.org");
        booker = new User(null, "Mike","mike.doe@example.org");

        request = new ItemRequest();
        request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("Нужен трактор");
        request.setRequester(booker);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("Навый трактор");
        item.setName("Трактор");
        item.setOwner(owner);
        item.setRequest(request);

        bookingPast = new Booking(
                null,
                LocalDateTime.of(2022, 5,7,12,0),
                LocalDateTime.of(2022,5,9,10,0),
                item,
                booker,
                BookingStatus.REJECTED);

        bookingFuture = new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.APPROVED);

        bookingCurrent = new Booking(
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.APPROVED);

        userRepository.save(owner);
        userRepository.save(booker);
        requestRepository.save(request);
        itemRepository.save(item);
        bookingRepository.save(bookingPast);
        bookingRepository.save(bookingCurrent);
        bookingRepository.save(bookingFuture);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(Long, Long, BookingStatus)}
     */
    @Test
    void findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(
                item.getId(),
                owner.getId(),
                BookingStatus.REJECTED);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findDistinctBookingByBookerIdAndItemId(Long, Long)}
     */
    @Test
    void findDistinctBookingByBookerIdAndItemId() {
        List<Booking> actualBookings = bookingRepository
                .findDistinctBookingByBookerIdAndItemId(booker.getId(), item.getId());

        assertEquals(List.of(bookingCurrent,bookingPast), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByBookerIdOrderByStartDesc(Long, Pageable)}
     */
    @Test
    void findByBookerIdOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findByBookerIdOrderByStartDesc(booker.getId(), page);

        assertEquals(List.of(bookingFuture, bookingCurrent, bookingPast), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByBookerIdAndEndIsBeforeOrderByStartDesc(Long, LocalDateTime, Pageable)}
     */
    @Test
    void findByBookerIdAndEndIsBeforeOrderByStartDesc() {
        PageRequest page = PageRequest.of(0, 4);
        List<Booking> actualBookings = bookingRepository
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), page);

        assertEquals(List.of(bookingPast), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
     * Long, LocalDateTime, LocalDateTime, Pageable)}
     */
    @Test
    void findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        booker.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);


        assertEquals(List.of(bookingFuture), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
     * Long, LocalDateTime, LocalDateTime, Pageable)}
     */
    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                booker.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                page);

        assertEquals(List.of(bookingCurrent), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByBookerIdAndStatusOrderByStartDesc(Long, BookingStatus, Pageable)}
     */
    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.APPROVED, page);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findAllByItemOwnerIdOrderByStartDesc(Long, Pageable)}
     */
    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), page);

        assertEquals(List.of(bookingFuture,bookingCurrent,bookingPast), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findAllByItemOwnerIdAndStatusNotOrderByStartDesc(Long, BookingStatus)}
     */
    @Test
    void findAllByItemOwnerIdAndStatusNotOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(owner.getId(), BookingStatus.REJECTED);

        assertEquals(List.of(bookingFuture,bookingCurrent), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
     * Long, LocalDateTime, LocalDateTime, Pageable)}
     */
    @Test
    void findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                owner.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                page);

        assertEquals(List.of(bookingFuture),actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long, LocalDateTime, Pageable)}
     */
    @Test
    void findByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                owner.getId(),
                LocalDateTime.now(),
                page);

        assertEquals(List.of(bookingPast), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
     * Long, LocalDateTime, LocalDateTime, Pageable)}
     */
    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                owner.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                page);

        assertEquals(List.of(bookingCurrent), actualBookings);
    }

    /**
     * Method under test:
     * {@link BookingRepository#findByItemOwnerIdAndStatusOrderByStartDesc(Long, BookingStatus, Pageable)}
     */
    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.APPROVED, page);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }
}