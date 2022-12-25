package ru.practicum.shareit.user.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class UserValidatorTest {
    @Test
    void shouldPassValidObjects() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());
        ItemValidator.validate(screwdriverCreationDto);
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {
        User john = new User(1L, "JOHN", "");

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsNull() {
        User john = new User(1L, "JOHN", null);

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsIncorrect() {
        User john = new User(1L, "JOHN", "на деревню дедушке");

        Assertions.assertThrows(ValidationException.class,
                () -> UserValidator.validate(john));
    }
}
