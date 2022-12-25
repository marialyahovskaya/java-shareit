package ru.practicum.shareit.item.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;

public class ItemValidatorTest {

    @Test
    void shouldPassValidObjects() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());
        ItemValidator.validate(screwdriverCreationDto);
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsEmpty() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, "", "nnnnnnn", 2L, true, null, null, new ArrayList<>());

        Assertions.assertThrows(ValidationException.class,
                () -> ItemValidator.validate(screwdriverCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsNull() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, null, "nnnnnnn", 2L, true, null, null, new ArrayList<>());

        Assertions.assertThrows(ValidationException.class,
                () -> ItemValidator.validate(screwdriverCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsEmpty() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, "отвертка", "", 2L, true, null, null, new ArrayList<>());

        Assertions.assertThrows(ValidationException.class,
                () -> ItemValidator.validate(screwdriverCreationDto));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsNull() {
        ItemDto screwdriverCreationDto = new ItemDto(
                null, "отвертка", null, 2L, true, null, null, new ArrayList<>());

        Assertions.assertThrows(ValidationException.class,
                () -> ItemValidator.validate(screwdriverCreationDto));
    }
}
