package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items", schema = "public")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments = new ArrayList<>();

}
