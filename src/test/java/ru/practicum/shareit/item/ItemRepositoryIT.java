package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRepositoryIT {
    @Autowired
    private ItemRepository itemRepository;


    @BeforeEach
    void addItems() {
        itemRepository.save(new Item(null, 1L, "отвертка", "nnnnnnn", 2L, true, new ArrayList<>()));
        itemRepository.save(new Item(null, 2L, "дрель", "оооооооо", 1L, true, new ArrayList<>()));
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        List<Item> actualItems = itemRepository.findByOwnerIdOrderByIdAsc(1L);
        assertThat(actualItems, hasSize(1));
    }

    @Test
    void findByRequestId() {
        List<Item> actualItems = itemRepository.findByRequestId(1L);
        assertThat(actualItems, hasSize(1));
    }

    @AfterEach
    void deleteItems() {
        itemRepository.deleteAll();
    }
}