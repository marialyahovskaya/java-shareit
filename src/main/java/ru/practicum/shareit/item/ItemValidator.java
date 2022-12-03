package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
public class ItemValidator {

    public static void validate(final ItemDto item) throws ValidationException {
        if (item.getName() == null || item.getName().equals("")) {
            log.info("Name is empty");
            throw new ValidationException("Name is empty.");
        }
        if (item.getDescription() == null || item.getDescription().equals("")) {
            log.info("Description is empty");
            throw new ValidationException("Description is empty.");
        }
    }
}