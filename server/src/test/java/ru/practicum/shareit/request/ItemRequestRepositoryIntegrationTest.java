package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.PaginationHelper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRequestRepositoryIntegrationTest {

    private Long johnId;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void addItemRequests() {

        User john = new User(null, "JOHN", "john@email.com");
        User jack = new User(null, "JACK", "jack@email.com");
        User peter = new User(null, "PETER", "peter@email.com");
        userRepository.save(john);
        johnId = john.getId();
        userRepository.save(jack);
        userRepository.save(peter);
        itemRequestRepository.save(new ItemRequest(null, "Дайте дрель", john, null));
        itemRequestRepository.save(new ItemRequest(null, "Дайте отвертку", jack, null));
        itemRequestRepository.save(new ItemRequest(null, "Дайте гитару", peter, null));
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> actualItemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(johnId);
        assertThat(actualItemRequests, hasSize(1));
    }

    @Test
    void findByRequestorIdNot() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<ItemRequest> actualItemRequests = itemRequestRepository.findByRequestorIdNot(johnId, pageable);
        assertThat(actualItemRequests, hasSize(2));
    }

    @AfterEach
    void deleteItemRequests() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }
}