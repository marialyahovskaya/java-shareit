package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRepositoryIntegrationTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Long drillRequestId;


    @BeforeEach
    void addItems() {

        User jack = new User(null, "JACK", "jack@email.com");
        User john = new User(null, "JOHN", "john@email.com");
        userRepository.save(jack);
        userRepository.save(john);

        ItemRequest drillRequest = new ItemRequest(null, "Дайте дрель", jack, LocalDateTime.now());
        ItemRequest screwdriverRequest = new ItemRequest(null, "Дайте отвертку", john, LocalDateTime.now());
        itemRequestRepository.save(drillRequest);
        itemRequestRepository.save(screwdriverRequest);
        this.drillRequestId = drillRequest.getId();
        itemRepository.save(new Item(null, jack, "отвертка", "nnnnnnn", screwdriverRequest, true, new ArrayList<>()));
        itemRepository.save(new Item(null, john, "дрель", "оооооооо", drillRequest, true, new ArrayList<>()));
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        List<Item> actualItems = itemRepository.findByOwnerIdOrderByIdAsc(1L);
        assertThat(actualItems, hasSize(1));
    }

    @Test
    void findByRequestId() {
        List<Item> actualItems = itemRepository.findByRequestId(drillRequestId);
        assertThat(actualItems, hasSize(1));
    }

    @AfterEach
    void deleteItems() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

    }
}