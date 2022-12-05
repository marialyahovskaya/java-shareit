package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByItemUserIdOrderByStartDesc(Long userId);

    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long id, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long id, LocalDateTime end);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime end);
}