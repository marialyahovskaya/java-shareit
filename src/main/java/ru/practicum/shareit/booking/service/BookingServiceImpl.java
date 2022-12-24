package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    public BookingDto addBooking(Long userId, BookingCreationDto bookingCreationDto) {
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


        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
        if (!booking.get().getBooker().getId().equals(userId) &&
                !booking.get().getItem().getUserId().equals(userId)) {
            throw new NotFoundException("Booking not found");
        }
        return BookingMapper.toBookingDto(booking.get());
    }

    @Override
    public Collection<BookingDto> findBookingsByBookerId(Long userId, String state, int from, int size) {
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
        if (size == 0) {
            throw new ValidationException("Size is zero");
        }
        if (from < 0) {
            throw new ValidationException("from cannot be negative");
        }
        Pageable pageable = PaginationHelper.makePageable(from, size);
        switch (requestedState) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerOrderByStartDesc(user.get(), pageable));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.get(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user.get(), LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(user.get(), LocalDateTime.now(), pageable));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerAndStatusOrderByStartDesc(user.get(), BookingState.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerAndStatusOrderByStartDesc(user.get(), BookingState.REJECTED, pageable));
        }
        throw new ValidationException("Invalid booking state");
    }

    @Override
    public Collection<BookingDto> findBookingsByOwnerId(Long userId, String state, int from, int size) {
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
        if (size == 0) {
            throw new ValidationException("Size is zero");
        }
        if (from < 0) {
            throw new ValidationException("From cannot be negative");
        }
        Pageable pageable = PaginationHelper.makePageable(from, size);
        switch (requestedState) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdOrderByStartDesc(userId, pageable));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdAndStatusOrderByStartDesc(userId, BookingState.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository.findByItemUserIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED, pageable));
        }
        throw new ValidationException("Invalid booking state");

    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
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
        return BookingMapper.toBookingDto(bookingRepository.save(bookingToSave));
    }
}
