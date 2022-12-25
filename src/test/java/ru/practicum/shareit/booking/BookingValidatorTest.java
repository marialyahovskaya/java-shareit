package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidatorTest {

    @Test
    void shouldPassValidObjects() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, start, end);

        BookingValidator.validate(screwdriverBookingCreationDto);
    }

    @Test
    void shouldThrowValidationExceptionWhenItemIdIsMissing() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(null, start, end);

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validate(screwdriverBookingCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartMissing() {
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, null, end);

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validate(screwdriverBookingCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenEndMissing() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, start, null);

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validate(screwdriverBookingCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartIsInThePast() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, start, end);

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validate(screwdriverBookingCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingCreationDto screwdriverBookingCreationDto = new BookingCreationDto(1L, start, end);

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validate(screwdriverBookingCreationDto));
    }
}
