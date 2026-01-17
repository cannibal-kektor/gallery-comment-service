package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.aspect.PublishCommentEvent;
import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.dto.UserDto;
import kektor.innowise.gallery.comment.exception.ImageNotFoundException;
import kektor.innowise.gallery.comment.mapper.CommentMapper;
import kektor.innowise.gallery.comment.model.Comment;
import kektor.innowise.gallery.comment.repository.CommentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kektor.innowise.gallery.comment.msg.CommentEventMessage.EventType.ADD_COMMENT;
import static kektor.innowise.gallery.comment.msg.CommentEventMessage.EventType.REMOVE_COMMENT;
import static kektor.innowise.gallery.comment.msg.CommentEventMessage.EventType.UPDATE_COMMENT;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository repository;
    ImageServiceClient imageService;
    CommentMapper mapper;
    UserServiceClient userService;
    SecurityService securityService;

    @Transactional(readOnly = true)
    public CommentDto getById(Long commentId) {
        Comment comment = repository.findByIdExceptionally(commentId);
        return toDto(comment);
    }

    @PublishCommentEvent(ADD_COMMENT)
    @Transactional
    public CommentDto postNewComment(Long imageId, CreateCommentDto newCommentDto) {
        checkImageExists(imageId);
        Comment comment = mapper.toModel(newCommentDto, imageId, currentUserId());
        comment = repository.save(comment);
        return toDto(comment);
    }

    @PublishCommentEvent(UPDATE_COMMENT)
    @Transactional
    public CommentDto updateComment(Long commentId, CreateCommentDto newComment) {
        Comment comment = repository.findByIdAuthorized(commentId, currentUserId());
        comment.setContent(newComment.content());
        comment = repository.save(comment);
        return toDto(comment);
    }

    @PublishCommentEvent(REMOVE_COMMENT)
    @Transactional
    public CommentDto deleteComment(Long commentId) {
        Comment comment = repository.findByIdAuthorized(commentId, currentUserId());
        repository.deleteById(commentId);
        return toDto(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getImageComments(Long imageId, Pageable pageable) {
        checkImageExists(imageId);
        return repository.findCommentsByImageId(imageId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public void deleteImageComments(Long imageId) {
        repository.deleteCommentsByImageId(imageId);
    }

    private void checkImageExists(Long imageId) {
        if (imageService.checkImageExists(imageId)
                .getStatusCode().isError())
            throw new ImageNotFoundException(imageId);
    }

    private CommentDto toDto(Comment comment) {
        String username = userService.fetchUser(comment.getUserId())
                .map(UserDto::username)
                .orElse(null);
        return mapper.toDto(comment, username);
    }

    Long currentUserId() {
        return securityService.currentUserId();
    }
}

