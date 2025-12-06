package kektor.innowise.gallery.comment.mapper;

import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.model.Comment;
import kektor.innowise.gallery.comment.msg.CommentEventMessage;
import kektor.innowise.gallery.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapConfig.class)
public interface CommentMapper {

    Comment toModel(CreateCommentDto commentDto, Long imageId, Long userId);

    CommentDto toDto(Comment comment, String username);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "comment", source = "comment.content")
    @Mapping(target = "instant", expression = "java(Instant.now())")
    CommentEventMessage toEvent(CommentDto comment, UserPrincipal user, CommentEventMessage.EventType eventType);

}
