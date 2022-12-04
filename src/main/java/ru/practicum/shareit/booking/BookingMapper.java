package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

}
