package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.PaginationHelper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRequestRepositoryIntegrationTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @BeforeEach
    void addItemRequests() {
        itemRequestRepository.save(new ItemRequest(null, "Дайте дрель", 1L, null));
        itemRequestRepository.save(new ItemRequest(null, "Дайте отвертку", 2L, null));
        itemRequestRepository.save(new ItemRequest(null, "Дайте гитару", 3L, null));
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> actualItemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L);
        assertThat(actualItemRequests, hasSize(1));
    }

    @Test
    void findByRequestorIdNot() {
        Pageable pageable = PaginationHelper.makePageable(0, 100);
        List<ItemRequest> actualItemRequests = itemRequestRepository.findByRequestorIdNot(1L, pageable);
        assertThat(actualItemRequests, hasSize(2));
    }

    @AfterEach
    void deleteItemRequests() {
        itemRequestRepository.deleteAll();
    }
}