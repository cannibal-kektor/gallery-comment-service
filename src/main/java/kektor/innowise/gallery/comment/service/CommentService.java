package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.dto.UserDto;
import kektor.innowise.gallery.comment.exception.ImageNotFoundException;
import kektor.innowise.gallery.comment.mapper.CommentMapper;
import kektor.innowise.gallery.comment.model.Comment;
import kektor.innowise.gallery.comment.repository.CommentRepository;
import kektor.innowise.gallery.security.UserPrincipal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository repository;
    ImageServiceClient imageService;
    CommentMapper mapper;
    UserServiceClient userService;

    @Transactional(readOnly = true)
    public CommentDto getById(Long commentId) {
        Comment comment = repository.findByIdExceptionally(commentId);
        return toDto(comment);
    }

    @Transactional
    public CommentDto postNewComment(Long imageId, CreateCommentDto newCommentDto) {
        checkImageExists(imageId);
        Comment comment = mapper.toModel(newCommentDto, imageId, currentUserId());
        comment = repository.save(comment);
        return toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CreateCommentDto newComment) {
        Comment comment = repository.findByIdAuthorized(commentId, currentUserId());
        comment.setContent(newComment.content());
        comment = repository.save(comment);
        return toDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        repository.findByIdAuthorized(commentId, currentUserId());
        repository.deleteById(commentId);
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
        return ((UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .id();
    }
}

