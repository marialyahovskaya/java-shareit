package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    @SneakyThrows
    @Test
    void createItem() {
        long sharerUserId = 1L;
        ItemDto itemToCreate = new ItemDto(null, "Дрель", "Ударная дрель", null, true, null, null, null);
        ItemDto createdItem = new ItemDto(1L, "Дрель", "Ударная дрель", null, true, null, null, null);
        when(itemService.addItem(sharerUserId, itemToCreate)).thenReturn(createdItem);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", sharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(createdItem), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        long sharerUserId = 1L;
        long itemId = 1L;
        ItemDto itemToUpdate = new ItemDto(1L, "Дрель", "Ударная дрель", null, true, null, null, null);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", sharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isOk());

        verify(itemService).patchItem(sharerUserId, itemId, itemToUpdate);
    }

    @SneakyThrows
    @Test
    void findItemById() {
        long sharerUserId = 1L;
        long itemId = 1L;
        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemService).findById(sharerUserId, itemId);
    }

    @SneakyThrows
    @Test
    void findItemsByOwnerId() {
        long sharerUserId = 1L;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemService).findItemsByOwnerId(sharerUserId);
    }

    @SneakyThrows
    @Test
    void search() {
        long sharerUserId = 1L;
        String text = "дрел";
        mockMvc.perform(get("/items/search",text)
                        .param("text", text)
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemService).search(text);
    }

    @SneakyThrows
    @Test
    void createComment() {
        long sharerUserId = 1L;
        CommentCreationDto commentToCreate = new CommentCreationDto("Вот");
        CommentDto createdComment = new CommentDto(1L, "Вот", "John", LocalDateTime.now());
        long itemId = 2L;
        when(itemService.addComment(sharerUserId, itemId, commentToCreate)).thenReturn(createdComment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", sharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(createdComment), result);
    }
}
