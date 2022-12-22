package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.ValidationException;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingCreationDto bookingCreationDto)
            throws ValidationException {
        return new ResponseEntity<>(bookingService.addBooking(userId, bookingCreationDto), HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) throws ValidationException {
        return new ResponseEntity<>(bookingService.findBooking(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> findBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                                         @RequestParam(required = false, defaultValue = "0") int from,
                                                                         @RequestParam(required = false, defaultValue = "100") int size) {
        return new ResponseEntity<>(bookingService.findBookingsByBookerId(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> findBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.findBookingsByOwnerId(userId, state), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return new ResponseEntity<>(bookingService.updateStatus(userId, bookingId, approved), HttpStatus.OK);
    }

}
