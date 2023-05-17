package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User user;

    private ItemRequest itemRequest;

    private Item item;

    private Item item2;

    @BeforeEach
    void setUp() {
        user = new User(null, "Mike", "mike@gmail.com");
        userRepository.save(user);

        itemRequest = new ItemRequest(
                null,
                "Нужен трактор",
                user,
                LocalDateTime.of(2023, 5, 5, 17, 15, 30));
        requestRepository.save(itemRequest);

        item = new Item(null,
                user,"Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                itemRequest);
        item2 = new Item(null,
                user,"Отвртка",
                "Аккумуляторная отвертка",
                true,
                null);
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    /**
     * Method under test: {@link ItemRepository#findAllByOwnerIdOrderById(Long, Pageable)}
     */
    @Test
    void findAllByOwnerIdOrderById() {
        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderById(user.getId(), PageRequest.of(0, 2));

        assertEquals(List.of(item, item2), actualItems);
    }

    /**
     * Method under test: {@link ItemRepository#searchByText(String, Pageable)}
     */
    @Test
    void searchByText() {
        String text = "кКуМ";
        List<Item> items = itemRepository.searchByText(text, PageRequest.of(0, 4));

        assertEquals(List.of(item, item2), items);
    }

    /**
     * Method under test: {@link ItemRepository#findAllByRequestIdIn(List)}
     */
    @Test
    void findAllByRequestIdIn() {
        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertEquals(List.of(item), actualItems);
    }

    /**
     * Method under test: {@link ItemRepository#findAllByRequestId(Long)}
     */
    @Test
    void findAllByRequestId() {
        List<Item> actualItems = itemRepository.findAllByRequestId(itemRequest.getId());

        assertEquals(List.of(item), actualItems);
    }
}
