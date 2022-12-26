package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIT {

    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void addItem() {

        User jack = new User(null, "JACK", "jack@email.com");

        em.persist(jack);

        em.flush();

        ItemDto screwdriverDto = new ItemDto(
                1L, "отвертка", "nnnnnnn", 2L, true, null, null, new ArrayList<>());


        itemService.addItem(jack.getId(), screwdriverDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name=:name", Item.class);
        Item item = query.setParameter("name", screwdriverDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getOwnerId(), equalTo(jack.getId()));
        assertThat(item.getName(), equalTo(screwdriverDto.getName()));
        assertThat(item.getDescription(), equalTo(screwdriverDto.getDescription()));
        assertThat(item.getRequestId(), equalTo(screwdriverDto.getRequestId()));
        assertThat(item.getAvailable(), equalTo(screwdriverDto.getAvailable()));
        assertThat(item.getComments(), hasSize(0));

    }
}
