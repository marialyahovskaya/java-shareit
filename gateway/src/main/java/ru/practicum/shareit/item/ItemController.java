package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long id,
                                              @RequestBody @Valid PatchItemDto itemDto) {
        return itemClient.patchItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long id) {
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.findItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long itemId,
                                                    @RequestBody @Valid CommentCreationDto commentCreationDto) {
        return itemClient.addComment(userId, itemId, commentCreationDto);
    }
}