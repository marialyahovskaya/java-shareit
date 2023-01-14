package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.findItemRequestsByRequestorId(userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long id) {
        return itemRequestClient.findItemRequestById(userId, id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                          @Positive @RequestParam(required = false, defaultValue = "100") int size) {
        return itemRequestClient.findAll(userId, from, size);
    }
}
