package ru.practicum.shareit.user.model;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    private String email;
}