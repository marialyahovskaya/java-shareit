package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.ValidationException;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingCreationDto bookingCreationDto)
            throws ValidationException {
        return bookingService.addBooking(userId, bookingCreationDto);
    }

    @GetMapping("/{bookingId}")
    public Booking findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) throws ValidationException {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<Booking> findBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public Collection<Booking> findBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findBookingsByOwnerId(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

}
