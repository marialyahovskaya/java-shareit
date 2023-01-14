package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank
    private String description;

    private Long requestorId;
    private LocalDateTime created;
    private Collection<ItemDto> items;
}
