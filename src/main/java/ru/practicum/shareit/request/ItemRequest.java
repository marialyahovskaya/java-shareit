package ru.practicum.shareit.request;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests", schema = "public")
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

    @CreationTimestamp
    private LocalDateTime created;
}
