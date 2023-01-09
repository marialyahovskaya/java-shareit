package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;

    private User owner;

    private String name;

    private String description;

    private ItemRequest request;

    private Boolean available;

    private List<Comment> comments = new ArrayList<>();

}
