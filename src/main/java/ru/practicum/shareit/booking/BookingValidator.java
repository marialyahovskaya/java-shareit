package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Slf4j
public class BookingValidator {

    public static void validate(final BookingCreationDto creationDto) throws ValidationException {
        if (creationDto.getItemId() == null) {
            log.info("ItemId is empty");
            throw new ValidationException("ItemId is empty");
        }
        if (creationDto.getStart() == null) {
            log.info("start is empty");
            throw new ValidationException("start is empty");
        }
        if (creationDto.getEnd() == null) {
            log.info("end is empty");
            throw new ValidationException("end is empty");
        }
        LocalDateTime now = LocalDateTime.now();
        if (creationDto.getStart().isBefore(now)) {
            log.info("bookingStart can't be in past");
            throw new ValidationException("bookingStart can't be in past");
        }
        if (creationDto.getEnd().isBefore(now)) {
            log.info("bookingEnd can't be in past");
            throw new ValidationException("bookingEnd can't be in past");
        }
        if (creationDto.getStart().isAfter(creationDto.getEnd())) {
            log.info("start can't be after end");
            throw new ValidationException("start can't be after end");
        }
    }
}
