package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final String headerShareUserId = "X-Sharer-User-Id";

    private final Long userId = 1L;

    private final Long itemId = 1L;

    @Test
    @DisplayName("MockMvcTest for addNewItem")
    void addNewItem_invalidData_returnsBadRequest() throws Exception {
        ItemDto invalidItem = new ItemDto();
        mvc.perform(post("/items/")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(invalidItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = List.of("The name cannot be empty!", "The description cannot be empty!");
                    List<String> actualErrors = bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.contains(actualErrors.get(0)) && !errors.contains(actualErrors.get(1))) {
                        throw new AssertionError("Expected " + errors + ", but got " + actualErrors);
                    }
                })
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addNewItem(userId, invalidItem);
    }

    @Test
    @DisplayName("MockMvc test for addNewComment method")
    void addNewComment_invalidInput_returnsBadRequest() throws Exception {
        CommentDto commentWithBlankText = new CommentDto();

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(commentWithBlankText))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    BindingResult bindingResult = exception.getBindingResult();
                    List<String> errors = Collections.singletonList("The text comment should not be empty.");
                    List<String> actualErrors = bindingResult.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());
                    if (!errors.equals(actualErrors)) {
                        throw new AssertionError("Expected " + errors + ", but got " + actualErrors);
                    }
                })
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).addNewComment(userId, itemId, commentWithBlankText);
    }

    @Test
    @DisplayName("Mock mvc test for getItemByUserId")
    void getItemByUserId_invalidParam_returnBadRequest() throws Exception {
        String error = "from: must be greater than or equal to 0. size: must be greater than or equal to 1.";

        String response = mvc.perform(get("/items")
                        .header(headerShareUserId, userId)
                        .param("text", "дрель")
                        .param("from", "-1")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        assertEquals(error, actualError.getError());
        verify(itemClient, never()).getItemByUserId(userId, -1, 1);
    }
}