package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {


    List<Booking> findByBookerOrderByStartDesc(User booker, Pageable pageable);


    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingState status, Pageable pageable);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User booker,
                                                                            LocalDateTime start,
                                                                            LocalDateTime end,
                                                                            Pageable pageable);


    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime end, Pageable pageable);


    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingState status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime end);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime end);

}