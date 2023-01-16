package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) {
        Comment comment = new Comment();
        comment.setText(commentCreationDto.getText());

        Item item = new Item();
        item.setId(itemId);
        comment.setItem(item);

        User user = new User();
        user.setId(userId);

        comment.setAuthor(user);

        return comment;
    }
}
