package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIT {

    private final EntityManager em;

    private final ItemRequestService itemRequestService;

    @Test
    void addItemRequest() {

        User john = new User(null, "JOHN", "john@email.com");

        em.persist(john);

        em.flush();

        ItemRequestDto guitarRequestCreationDto = new ItemRequestDto(null,
                "Дайте погонять гитару",
                john.getId(),
                null,
                null);

        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(john.getId(), guitarRequestCreationDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.description=:description", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", guitarRequestCreationDto.getDescription())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(guitarRequestCreationDto.getDescription()));
        assertThat(itemRequest.getRequestorId(), equalTo(guitarRequestCreationDto.getRequestorId()));
        assertThat(itemRequest.getCreated(), notNullValue());

    }

}
