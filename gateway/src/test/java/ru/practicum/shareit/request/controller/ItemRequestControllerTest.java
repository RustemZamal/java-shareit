package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestClient requestClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    @Test
    @DisplayName("MockMvcTest for createItemRequest method")
    void createItemRequest_whenInvalidRequest_thenReturnBadRequest() throws Exception {
        ItemRequestDto invalidRequest = new ItemRequestDto();
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

        assertEquals(error.getError(), mapper.readValue(response, ErrorResponse.class).getError());
        verify(requestClient, never()).createItemRequest(userId, invalidRequest);
    }

    @Test
    @DisplayName("MockMvcTest for getAllItemRequests method")
    void getAllItemRequests_whenInvalidParam_thenReturn_BadRequest() throws Exception {
        ErrorResponse error = new ErrorResponse("from: must be greater than or equal to 0. size: must be greater than or equal to 1.");
        String response = mvc.perform(get("/requests/all")
                        .header(headerShareUserId, userId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertEquals(error.getError(), mapper.readValue(response, ErrorResponse.class).getError());
        verify(requestClient, never()).getAllItemRequests(-1, 0, userId);

    }
}