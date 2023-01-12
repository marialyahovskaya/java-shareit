package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class UserValidator {

    public static void validate(final UserDto user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().equals("")) {
            log.info("Email is empty");
            throw new ValidationException("Email is empty.");
        }
        if (!user.getEmail().contains("@")) {
            log.info("Email must contain symbol \"@\"");
            throw new ValidationException("Email must contain symbol \"@\".");
        }
    }
}
