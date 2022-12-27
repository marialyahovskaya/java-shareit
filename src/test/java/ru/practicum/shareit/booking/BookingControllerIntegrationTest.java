package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void create() {
        Long johnId = 1L;
        Long jackId = 2L;
        User john = new User(johnId, "JOHN", "john@email.com");
        User jack = new User(jackId, "JACK", "jack@email.com");
        ItemRequest request = new ItemRequest(null, "Дайте дрель", john, LocalDateTime.now());

        Item screwdriver = new Item(
                1L, jack, "отвертка", "nnnnnnn", request, true, new ArrayList<>());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto bookingToCreate = new BookingCreationDto(1L, start, end);
        BookingDto createdBooking = new BookingDto(1L, start, end, screwdriver, johnId, john, BookingState.WAITING);
        when(bookingService.addBooking(johnId, bookingToCreate)).thenReturn(createdBooking);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", johnId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(createdBooking), result);
    }

    @SneakyThrows
    @Test
    void findBookingById() {
        long sharerUserId = 1L;
        long bookingId = 1L;
        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(bookingService).findBooking(sharerUserId, bookingId);
    }

    @SneakyThrows
    @Test
    void findBookingsByBookerId() {
        long sharerUserId = 1L;
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(bookingService).findBookingsByBookerId(sharerUserId, "ALL", 0, 100);
    }

    @SneakyThrows
    @Test
    void findBookingsByOwnerId() {
        long sharerUserId = 1L;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", sharerUserId))
                .andExpect(status().isOk());
        verify(bookingService).findBookingsByOwnerId(sharerUserId, "ALL", 0, 100);
    }

    @SneakyThrows
    @Test
    void updateStatus() {
        long sharerUserId = 1L;
        long bookingId = 1L;
        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", sharerUserId)
                        .param("approved", "true"))
                .andExpect(status().isOk());
        verify(bookingService).updateStatus(sharerUserId, bookingId, true);
    }

}
