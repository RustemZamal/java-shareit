package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment mapToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());

        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static List<CommentDto> mapToCommentDto(Iterable<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        if (comments == null) {
            return List.of();
        }

        for (Comment comment : comments) {
            dtos.add(mapToCommentDto(comment));
        }
        return dtos;
    }
}
