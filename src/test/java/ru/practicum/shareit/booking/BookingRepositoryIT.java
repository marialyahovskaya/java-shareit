package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    private Long jackId = 2L;
    private Long johnId = 1L;

    private User john = new User(johnId, "JOHN", "john@email.com");
    private Long itemId = 1L;
    @BeforeEach
    void addBookings() {

        Item screwdriver = new Item(
                itemId, jackId, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>());

        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2);

        Booking pastApprovedScrewdriverBooking = new Booking(
                null, pastStart, pastEnd, screwdriver, john, BookingState.APPROVED);
        Booking currentApprovedScrewdriverBooking = new Booking(
                null, pastStart, futureEnd, screwdriver, john, BookingState.APPROVED);
        Booking futureApprovedScrewdriverBooking = new Booking(
                null, futureStart, futureEnd, screwdriver, john, BookingState.APPROVED);
        Booking futureWaitingScrewdriverBooking = new Booking(
                null, futureStart, futureEnd, screwdriver, john, BookingState.WAITING);
        Booking futureRejectedScrewdriverBooking = new Booking(
                null, futureStart, futureEnd, screwdriver, john, BookingState.REJECTED);
        Booking futureCanceledScrewdriverBooking = new Booking(
                null, futureStart, futureEnd, screwdriver, john, BookingState.CANCELED);

        bookingRepository.save(pastApprovedScrewdriverBooking);
        bookingRepository.save(currentApprovedScrewdriverBooking);
        bookingRepository.save(futureApprovedScrewdriverBooking);
        bookingRepository.save(futureWaitingScrewdriverBooking);
        bookingRepository.save(futureRejectedScrewdriverBooking);
        bookingRepository.save(futureCanceledScrewdriverBooking);

    }

    @Test
    void findByBookerOrderByStartDesc() {

        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByBookerOrderByStartDesc(john, pageable);
        assertThat(actualBookings, hasSize(6));
    }

    @Test
    void findByBookerAndStatusOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(john, BookingState.APPROVED, pageable);

        assertThat(actualBookings, hasSize(3));
    }

    @Test
    void findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(john, LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(1));
    }

    @Test
    void findByBookerAndEndIsBeforeOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(john, LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(1));
    }

    @Test
    void findByBookerAndStartIsAfterOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(john, LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(4));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(jackId, pageable);

        assertThat(actualBookings, hasSize(6));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(jackId, BookingState.REJECTED,  pageable);

        assertThat(actualBookings, hasSize(1));
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(jackId, LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(1));
    }

    @Test
    void findByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(jackId, LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(1));
    }

    @Test
    void findByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(jackId, LocalDateTime.now(), pageable);

        assertThat(actualBookings, hasSize(4));
    }

    @Test
    void findFirstByItem_IdAndEndIsBeforeOrderByEndDesc() {
        Optional<Booking> actualBooking = bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now());

        assertThat(actualBooking.isPresent(),equalTo(true));
    }

    @Test
    void findFirstByItem_IdAndStartIsAfterOrderByStartAsc() {
        Optional<Booking> actualBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(itemId, LocalDateTime.now());

        assertThat(actualBooking.isPresent(),equalTo(true));
    }

    @Test
    void findByItem_IdAndEndIsBefore() {
        List<Booking> actualBookings = bookingRepository.findByItem_IdAndEndIsBefore(itemId, LocalDateTime.now());

        assertThat(actualBookings, hasSize(1));
    }
}