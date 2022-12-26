package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBookerOrderByStartDesc() {
    }

    @Test
    void findByBookerAndStatusOrderByStartDesc() {
    }

    @Test
    void findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
    }

    @Test
    void findByBookerAndEndIsBeforeOrderByStartDesc() {
    }

    @Test
    void findByBookerAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
    }

    @Test
    void findByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
    }

    @Test
    void findByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findFirstByItem_IdAndEndIsBeforeOrderByEndDesc() {
    }

    @Test
    void findFirstByItem_IdAndStartIsAfterOrderByStartAsc() {
    }

    @Test
    void findByItem_IdAndEndIsBefore() {
    }
}