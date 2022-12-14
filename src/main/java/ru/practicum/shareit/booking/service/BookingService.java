package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingCreationDto bookingCreationDto);

    BookingDto findBooking(Long userId, Long bookingId);

    Collection<BookingDto> findBookingsByBookerId(Long userId, String state);

    Collection<BookingDto> findBookingsByOwnerId(Long userId, String state);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);
}
