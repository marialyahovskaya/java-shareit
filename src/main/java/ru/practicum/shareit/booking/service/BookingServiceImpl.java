package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.enums.BookingRequestState;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Booking addBooking(Long userId, BookingCreationDto bookingCreationDto) {
        BookingValidator.validate(bookingCreationDto);
        Optional<Item> item = itemRepository.findById(bookingCreationDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (!item.get().getAvailable()) {
            throw new ValidationException("Cannot book unavailable item");
        }
        if (userId.equals(item.get().getUserId())) {
            throw new NotFoundException("You can't book this item");
        }

        Booking booking = BookingMapper.toBooking(userId, bookingCreationDto);
        booking.setItem(item.get());
        booking.setBooker(user.get());

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
        if (!booking.get().getBooker().getId().equals(userId) &&
                !booking.get().getItem().getUserId().equals(userId)) {
            throw new NotFoundException("Booking not found");
        }
        return booking.get();
    }

    @Override
    public Collection<Booking> findBookingsByBookerId(Long userId, String state) {
        BookingRequestState requestedState;
        try {
            requestedState = BookingRequestState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (requestedState == BookingRequestState.ALL) {
            return bookingRepository.findByBookerOrderByStartDesc(user.get());
        } else if (requestedState == BookingRequestState.CURRENT) {
            return bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.get(),
                    LocalDateTime.now(),
                    LocalDateTime.now());
        } else if (requestedState == BookingRequestState.PAST) {
            return bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user.get(), LocalDateTime.now());
        } else if (requestedState == BookingRequestState.FUTURE) {
            return bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(user.get(), LocalDateTime.now());
        } else if (requestedState == BookingRequestState.WAITING) {
            return bookingRepository.findByBookerAndStatusOrderByStartDesc(user.get(), BookingState.WAITING);
        } else if (requestedState == BookingRequestState.REJECTED) {
            return bookingRepository.findByBookerAndStatusOrderByStartDesc(user.get(), BookingState.REJECTED);
        }
        throw new ValidationException("Invalid booking state");
    }

    @Override
    public Collection<Booking> findBookingsByOwnerId(Long userId, String state) {
        BookingRequestState requestedState;
        try {
            requestedState = BookingRequestState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (requestedState == BookingRequestState.ALL) {
            return bookingRepository.findByItemUserIdOrderByStartDesc(userId);
        } else if (requestedState == BookingRequestState.CURRENT) {
            return bookingRepository.findByItemUserIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                    LocalDateTime.now(),
                    LocalDateTime.now());
        } else if (requestedState == BookingRequestState.PAST) {
            return bookingRepository.findByItemUserIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
        } else if (requestedState == BookingRequestState.FUTURE) {
            return bookingRepository.findByItemUserIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
        } else if (requestedState == BookingRequestState.WAITING) {
            return bookingRepository.findByItemUserIdAndStatusOrderByStartDesc(userId, BookingState.WAITING);
        } else if (requestedState == BookingRequestState.REJECTED) {
            return bookingRepository.findByItemUserIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED);
        }
        throw new ValidationException("Invalid booking state");

    }

    @Override
    public Booking updateStatus(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
        if (!userId.equals(booking.get().getItem().getUserId())) {
            throw new NotFoundException("Booking not found");
        }
        if (approved && booking.get().getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("Already approved");
        }
        Booking bookingToSave = booking.get();
        if (approved) {
            bookingToSave.setStatus(BookingState.APPROVED);
        } else {
            bookingToSave.setStatus(BookingState.REJECTED);
        }
        return bookingRepository.save(bookingToSave);
    }
}
