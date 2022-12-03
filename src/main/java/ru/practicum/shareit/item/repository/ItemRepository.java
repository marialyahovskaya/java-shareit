package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item addItem(final Item item);

    Item findItemById(final Long id);

    Collection<Item> findAll();

    Item updateItem(final Item item);

    Collection<Item> findItemsByUserId(final Long userId);
}
