package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toBooking(Long userId, BookingCreationDto bookingCreationDto) {
        Booking booking = new Booking();
        booking.setStart(bookingCreationDto.getStart());
        booking.setEnd(bookingCreationDto.getEnd());
        Item item = new Item();
        item.setId(bookingCreationDto.getItemId());
        booking.setItem(item);

        User user = new User();
        user.setId(userId);
        booking.setBooker(user);

        booking.setStatus(BookingState.WAITING);

        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .bookerId(booking.getBooker().getId())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Collection<BookingDto> toBookingDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
