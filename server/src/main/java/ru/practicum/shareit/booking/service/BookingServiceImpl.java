package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingCreationDto bookingCreationDto) {

        Item item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!item.getAvailable()) {
            throw new ValidationException("Cannot book unavailable item");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("You can't book this item");
        }

        Booking booking = BookingMapper.toBooking(userId, bookingCreationDto);
        booking.setItem(item);
        booking.setBooker(user);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        Boolean userIsBooker = booking.getBooker().getId().equals(userId);
        Boolean userIsOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!userIsBooker && !userIsOwner) {
            throw new NotFoundException("Booking not found");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findBookingsByBookerId(Long userId, String state, int from, int size) {
        BookingRequestState requestedState;
        requestedState = BookingRequestState.valueOf(state);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Pageable pageable = PaginationHelper.makePageable(from, size);
        switch (requestedState) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerOrderByStartDesc(user, pageable));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                user,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageable
                        ));
            case PAST:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByBookerAndEndIsBeforeOrderByStartDesc(user, LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByBookerAndStartIsAfterOrderByStartDesc(user, LocalDateTime.now(), pageable));
            case WAITING:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByBookerAndStatusOrderByStartDesc(user, BookingState.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByBookerAndStatusOrderByStartDesc(user, BookingState.REJECTED, pageable));
        }
        throw new ValidationException("Invalid booking state");
    }

    @Override
    public Collection<BookingDto> findBookingsByOwnerId(Long userId, String state, int from, int size) {
        BookingRequestState requestedState;
        requestedState = BookingRequestState.valueOf(state);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Pageable pageable = PaginationHelper.makePageable(from, size);
        switch (requestedState) {
            case ALL:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable));
            case CURRENT:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageable
                        ));
            case PAST:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case WAITING:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingState.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDto(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED, pageable));
        }
        throw new ValidationException("Invalid booking state");

    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Booking not found");
        }
        if (approved && booking.getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("Already approved");
        }
        Booking bookingToSave = booking;
        if (approved) {
            bookingToSave.setStatus(BookingState.APPROVED);
        } else {
            bookingToSave.setStatus(BookingState.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(bookingToSave));
    }
}
