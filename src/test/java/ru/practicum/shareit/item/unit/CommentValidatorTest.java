package ru.practicum.shareit.item.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentValidator;
import ru.practicum.shareit.item.dto.CommentCreationDto;

public class CommentValidatorTest {
    @Test
    void shouldPassValidObjects() {
        CommentCreationDto comment = new CommentCreationDto("Один один один");
        CommentValidator.validate(comment);
    }

    @Test
    void shouldThrowValidationExceptionWhenEmptyComment() {
        CommentCreationDto comment = new CommentCreationDto("");

        Assertions.assertThrows(ValidationException.class,
                () -> CommentValidator.validate(comment));
    }

    @Test
    void shouldThrowValidationExceptionWhenCommentIsNull() {
        CommentCreationDto comment = new CommentCreationDto(null);

        Assertions.assertThrows(ValidationException.class,
                () -> CommentValidator.validate(comment));
    }

}
