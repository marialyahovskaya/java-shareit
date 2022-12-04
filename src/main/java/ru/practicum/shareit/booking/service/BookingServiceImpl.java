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
        try {
            BookingRequestState requestedState = BookingRequestState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return bookingRepository.findByBookerOrderByStartDesc(user.get());
    }

    @Override
    public Collection<Booking> findBookingsByOwnerId(Long userId, String state) {
        try {
            BookingRequestState requestedState = BookingRequestState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return bookingRepository.findByItemUserIdOrderByStartDesc(userId);
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
