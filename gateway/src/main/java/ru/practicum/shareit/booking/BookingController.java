package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookingCreationDto bookingCreationDto)
            throws ValidationException {
        return bookingClient.addBooking(userId, bookingCreationDto);
    }
//
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<BookingDto> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                      @PathVariable Long bookingId) throws ValidationException {
//        return new ResponseEntity<>(bookingClient.findBooking(userId, bookingId), HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<Collection<BookingDto>> findBookingsByBookerId(
//            @RequestHeader("X-Sharer-User-Id") Long userId,
//            @RequestParam(required = false, defaultValue = "ALL") String state,
//            @RequestParam(required = false, defaultValue = "0") int from,
//            @RequestParam(required = false, defaultValue = "100") int size) {
//        return new ResponseEntity<>(bookingClient.findBookingsByBookerId(userId, state, from, size), HttpStatus.OK);
//    }
//
//    @GetMapping("/owner")
//    public ResponseEntity<Collection<BookingDto>> findBookingsByOwnerId(
//            @RequestHeader("X-Sharer-User-Id") Long userId,
//            @RequestParam(required = false, defaultValue = "ALL") String state,
//            @RequestParam(required = false, defaultValue = "0") int from,
//            @RequestParam(required = false, defaultValue = "100") int size) {
//        return new ResponseEntity<>(bookingClient.findBookingsByOwnerId(userId, state, from, size), HttpStatus.OK);
//    }
//
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId,
                                                   @RequestParam Boolean approved) {
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

}
