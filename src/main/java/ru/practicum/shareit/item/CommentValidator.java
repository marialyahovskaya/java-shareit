package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreationDto;

@Slf4j
public class CommentValidator {
    public static void validate(final CommentCreationDto comment) throws ValidationException {
        if (comment.getText() == null || comment.getText().equals("")) {
            log.info("Comment is empty");
            throw new ValidationException("Comment is empty.");
        }
    }
}