package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        Long johnId = 1L;
        Long jackId = 2L;

        User john = new User(johnId, "JOHN", "john@email.com");
        User jack = new User(jackId, "JACK", "jack@email.com");
        ItemRequest request = new ItemRequest(2L, "Дайте дрель", john, LocalDateTime.now());
        Item screwdriver = new Item(
                1L, jack, "отвертка", "nnnnnnn", request, true, new ArrayList<>());

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSS");
        String startString = start.format(dateTimeFormatter);
        String endString = end.format(dateTimeFormatter);

        BookingDto screwdriverBookingDto = new BookingDto(
                1L,
                start,
                end,
                screwdriver,
                johnId,
                john,
                BookingState.APPROVED);


        JsonContent<BookingDto> result = json.write(screwdriverBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").startsWith(startString);
        assertThat(result).extractingJsonPathStringValue("$.end").startsWith(endString);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.item.request.id").isEqualTo(2);
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.item.comments").hasSize(0);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("JOHN");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john@email.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
