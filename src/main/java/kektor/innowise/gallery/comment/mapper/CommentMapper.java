package kektor.innowise.gallery.comment.mapper;

import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.model.Comment;
import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class)
public interface CommentMapper {

    Comment toModel(CreateCommentDto commentDto, Long imageId, Long userId);

    CommentDto toDto(Comment comment, String username);

}
