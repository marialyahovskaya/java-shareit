package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void create() {
        long sharerUserId = 1L;
        long requestorId = 1L;
        ItemRequestDto requestToCreate = new ItemRequestDto(null, "Дайте дрель", requestorId, null, null);
        ItemRequestDto createdRequest = new ItemRequestDto(1L, "Дайте дрель", requestorId, null, null);
        when(itemRequestService.addItemRequest(requestorId, requestToCreate)).thenReturn(createdRequest);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", sharerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(createdRequest), result);
    }

    @SneakyThrows
    @Test
    void findItemRequestsByRequestorId() {
        long sharerUserId = 1L;
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemRequestService).findItemRequestsByRequestorId(sharerUserId);
    }

    @SneakyThrows
    @Test
    void findItemRequestByRequestId() {
        long sharerUserId = 1L;
        long requestId = 1L;
        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemRequestService).findItemRequestById(sharerUserId, requestId);
    }

    @SneakyThrows
    @Test
    void findAll() {
        long sharerUserId = 1L;
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(itemRequestService).findAll(sharerUserId, 0, 100);
    }

}
