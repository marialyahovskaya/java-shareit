package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public class UserValidatorTest {

    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {
        UserDto john = new UserDto(1L, "JOHN", "");

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsNull() {
        UserDto john = new UserDto(1L, "JOHN", null);

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsIncorrect() {
        UserDto john = new UserDto(1L, "JOHN", "на деревню дедушке");

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }
}
