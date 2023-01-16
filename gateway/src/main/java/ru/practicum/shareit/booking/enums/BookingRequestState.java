package ru.practicum.shareit.booking.enums;

import java.util.Optional;

public enum BookingRequestState {

    CURRENT, PAST, FUTURE, WAITING, REJECTED, ALL;

    public static Optional<BookingRequestState> from(String stringState) {
        for (BookingRequestState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
