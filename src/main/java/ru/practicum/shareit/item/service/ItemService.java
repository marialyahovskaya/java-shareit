package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(final Long userId, final ItemDto item);

    ItemDto patchItem(final Long userId, final Long id, final ItemDto itemDto);

    ItemDto findItemById(final Long userId, final Long id);

    Collection<ItemDto> findItemsByOwnerId(final Long userId);

    Collection<ItemDto> search(final String query);

    CommentDto addComment(final Long userId, final Long itemId, final CommentCreationDto commentCreationDto);
}
