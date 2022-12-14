package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingState status);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User booker,
                                                                            LocalDateTime start,
                                                                            LocalDateTime end);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime end);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime start);

    List<Booking> findByItemUserIdOrderByStartDesc(Long userId);

    List<Booking> findByItemUserIdAndStatusOrderByStartDesc(Long userId, BookingState status);

    List<Booking> findByItemUserIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);

    List<Booking> findByItemUserIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findByItemUserIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long id, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long id, LocalDateTime end);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime end);

}