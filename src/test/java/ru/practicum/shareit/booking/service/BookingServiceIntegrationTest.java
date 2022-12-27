package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void addBooking() {

        User john = new User(null, "JOHN", "john@email.com");
        em.persist(john);

        User jack = new User(null, "JACK", "jack@email.com");
        em.persist(jack);

        ItemRequest request = new ItemRequest(null, "Дайте дрель", john, LocalDateTime.now());
        em.persist(request);

        Item screwdriver =
                new Item(null, jack, "отвертка", "nnnnnnn", request, true, new ArrayList<>());

        em.persist(screwdriver);

        em.flush();

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(screwdriver.getId(), start, end);

        bookingService.addBooking(john.getId(), screwdriverBookingCreationDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b join b.item i where i.id=:itemId", Booking.class);
        Booking booking = query.setParameter("itemId", screwdriver.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(screwdriverBookingCreationDto.getStart()));
        assertThat(booking.getEnd(), equalTo(screwdriverBookingCreationDto.getEnd()));
        assertThat(booking.getItem(), equalTo(screwdriver));
        assertThat(booking.getBooker(), equalTo(john));
        assertThat(booking.getStatus(), equalTo(BookingState.WAITING));
    }
}
