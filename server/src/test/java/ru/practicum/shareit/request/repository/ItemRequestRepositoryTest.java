package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;

    private User requester;

    private ItemRequest itemRequest;

    private Item item;

    private Item item2;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Jane","jane.doe@example.org");
        requester = new User(null, "Mike","mike.doe@example.org");

        itemRequest = new ItemRequest(
                null,
                "Нужен трактор",
                requester,
                LocalDateTime.of(2023, 5, 5, 17, 15, 30));

        item = new Item(null,
                owner,"Нужен трактор",
                "Новый трактор",
                true,
                itemRequest);
        item2 = new Item(null,
                owner,"Отвртка",
                "Аккумуляторная отвертка",
                true,
                null);

        userRepository.save(requester);
        userRepository.save(owner);
        requestRepository.save(itemRequest);
        itemRepository.save(item);
        itemRepository.save(item2);

    }

    /**
     * Method under test: {@link ItemRequestRepository#findAllByRequesterIdOrderByCreatedDesc(Long)}
     */
    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> actualRequests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertEquals(List.of(itemRequest), actualRequests);
    }

    /**
     * Method under test: {@link ItemRequestRepository#findAllByRequesterIdNot(Long, Pageable)}
     */
    @Test
    void findAllByRequesterIdNot() {
        List<ItemRequest> actualRequests = requestRepository
                .findAllByRequesterIdNot(requester.getId(), PageRequest.of(0, 4));

        assertTrue(actualRequests.isEmpty());
    }
}