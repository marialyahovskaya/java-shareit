package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;

@WebMvcTest(BookingController.class)
public class BookingControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @SneakyThrows
    @Test
    void create() {

    }

    @SneakyThrows
    @Test
    void findBookingById() {

    }

    @SneakyThrows
    @Test
    void findBookingsByBookerId() {

    }

    @SneakyThrows
    @Test
    void findBookingsByOwnerId() {

    }

    @SneakyThrows
    @Test
    void updateStatus() {

    }
}
