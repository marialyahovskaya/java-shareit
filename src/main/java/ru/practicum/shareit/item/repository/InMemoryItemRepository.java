package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private Long nextId = 1L;

    private Long generateItemId() {
        return nextId++;
    }

    @Override
    public Item addItem(final Item item) {
        Long itemId = generateItemId();
        item.setId(itemId);
        items.put(itemId, item);

        return item;
    }

    @Override
    public Item findItemById(final Long id) {
        return items.get(id);
    }

    @Override
    public Item updateItem(final Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> findAll() {
        return items.values();
    }

    @Override
    public Collection<Item> findItemsByUserId(final Long userId) {
        return items.values().stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }
}