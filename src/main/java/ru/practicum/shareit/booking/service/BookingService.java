package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import java.util.Collection;

public interface BookingService {

    Booking addBooking(Long userId, BookingCreationDto bookingCreationDto);

    Booking findBooking(Long userId, Long bookingId);

    Collection<Booking> findBookingsByBookerId(Long userId, String state);

    Collection<Booking> findBookingsByOwnerId(Long userId, String state);

    Booking updateStatus(Long userId, Long bookingId, Boolean approved);
}
