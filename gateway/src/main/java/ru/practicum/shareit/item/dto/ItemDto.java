package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Long requestId;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;
}
