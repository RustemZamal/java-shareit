package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    private static ItemRequestDto requestDto;

    private static ItemDto itemDto;

    @BeforeAll
    static void setUp() {
        itemDto = new ItemDto(
                1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                2L);

        requestDto = new ItemRequestDto(
                1L,
                "Хотел бы воспользоваться Дрелью",
                LocalDateTime.now(),
                List.of(itemDto));
    }


    /**
     * Method under test: {@link ItemRequestController#createItemRequest(Long, ItemRequestDto)}
     */
    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(requestDto);

        String response = mvc.perform(post("/requests")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(requestDto, mapper.readValue(response, ItemRequestDto.class));
    }

    /**
     * Method under test: {@link ItemRequestController#createItemRequest(Long, ItemRequestDto)}
     */
    @Test
    void createItemRequest_whenInvalidRequest_thenReturnBadRequest() throws Exception {
        ItemRequestDto invalidRequest = new ItemRequestDto(
                1L,
                null,
                LocalDateTime.now(),
                List.of());
        ErrorResponse error = new ErrorResponse("description cannot be empty or null.");

        String response = mvc.perform(post("/requests")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(error, mapper.readValue(response, ErrorResponse.class));
    }

    /**
     * Method under test: {@link ItemRequestController#getItemRequestByOwnerId(Long)}
     */
    @Test
    void getItemRequestByOwnerId() throws Exception {
        when(itemRequestService.getItemRequestByOwnerId(anyLong())).thenReturn(List.of(requestDto));

        String response = mvc.perform(get("/requests")
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<ItemRequestDto> actualRequests = mapper.readValue(response, new TypeReference<>() {});

        assertEquals(List.of(requestDto), actualRequests);
    }

    /**
     * Method under test: {@link ItemRequestController#getAllItemRequests(Long, Integer, Integer)}
     */
    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(0, 2, userId)).thenReturn(List.of(requestDto));

        String response = mvc.perform(get("/requests/all")
                        .header(headerShareUserId, userId)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<ItemRequestDto> actualRequests = mapper.readValue(response, new TypeReference<>() {});

        assertEquals(List.of(requestDto), actualRequests);
    }

    /**
     * Method under test: {@link ItemRequestController#getItemRequestById(Long, Long)}
     */
    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(requestDto);

        String response = mvc.perform(get("/requests/{id}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(requestDto, mapper.readValue(response, ItemRequestDto.class));
    }
}