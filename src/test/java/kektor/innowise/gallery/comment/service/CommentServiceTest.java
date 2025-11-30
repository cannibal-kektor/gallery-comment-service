package kektor.innowise.gallery.comment.service;

import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.dto.UserDto;
import kektor.innowise.gallery.comment.exception.CommentNotFoundException;
import kektor.innowise.gallery.comment.exception.ImageNotFoundException;
import kektor.innowise.gallery.comment.exception.NonAuthorizedCommentAccessException;
import kektor.innowise.gallery.comment.mapper.CommentMapper;
import kektor.innowise.gallery.comment.model.Comment;
import kektor.innowise.gallery.comment.repository.CommentRepository;
import kektor.innowise.gallery.security.HeaderAuthenticationToken;
import kektor.innowise.gallery.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    CommentRepository repository;
    @Mock
    ImageServiceClient imageService;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    CommentMapper mapper;

    @InjectMocks
    CommentService commentService;

    final Long commentId = 1L;
    final Long imageId = 1L;
    final Long userId = 1L;
    final String content = "Test content";
    final Instant instant = Instant.now();
    final String testUsername = "testUsername";
    final String testEmail = "testEmail";

    Comment testComment;
    CommentDto testCommentDto;
    CreateCommentDto testNewCommentDto;
    UserDto testUserDto;

    @BeforeEach
    void setUpTestData() {
        testComment = new Comment();
        testComment.setId(commentId);
        testComment.setUserId(userId);
        testComment.setImageId(imageId);
        testComment.setContent(content);
        testComment.setCreatedAt(instant);
        testCommentDto = new CommentDto(commentId, userId, testUsername, imageId, content, instant);
        testNewCommentDto = new CreateCommentDto(content);
        testUserDto = new UserDto(userId, testUsername, testEmail);
    }

    @BeforeEach
    void setUpSecurity() {
        UserPrincipal userPrincipal = new UserPrincipal(userId, testUsername, testEmail);
        HeaderAuthenticationToken authenticationToken = new HeaderAuthenticationToken(userPrincipal);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void returnCommentDto_When_CommentExists() {
        when(repository.findByIdExceptionally(commentId)).thenReturn(testComment);
        when(userServiceClient.fetchUser(userId)).thenReturn(Optional.of(testUserDto));
        when(mapper.toDto(testComment, testUsername)).thenReturn(testCommentDto);

        CommentDto result = commentService.getById(commentId);

        assertNotNull(result);
        assertEquals(testCommentDto, result);
        verify(repository).findByIdExceptionally(commentId);
        verify(userServiceClient).fetchUser(userId);
        verify(mapper).toDto(testComment, testUsername);
    }

    @Test
    void throwCommentNotFoundException_When_CommentDoesNotExist() {
        when(repository.findByIdExceptionally(commentId))
                .thenThrow(new CommentNotFoundException(commentId));

        assertThrows(CommentNotFoundException.class, () -> commentService.getById(commentId));
        verify(repository).findByIdExceptionally(commentId);
        verify(mapper, never()).toDto(any(), any());
    }

    @Test
    void postAndReturnCommentDto_When_ValidDataProvided() {
        when(imageService.checkImageExists(imageId)).thenReturn(ResponseEntity.ok().build());
        when(mapper.toModel(testNewCommentDto, imageId, userId)).thenReturn(testComment);
        when(repository.save(testComment)).thenReturn(testComment);
        when(userServiceClient.fetchUser(userId)).thenReturn(Optional.of(testUserDto));
        when(mapper.toDto(testComment, testUsername)).thenReturn(testCommentDto);

        CommentDto result = commentService.postNewComment(imageId, testNewCommentDto);

        assertNotNull(result);
        assertEquals(testCommentDto, result);
        verify(imageService).checkImageExists(imageId);
        verify(mapper).toModel(testNewCommentDto, imageId, userId);
        verify(repository).save(testComment);
        verify(userServiceClient).fetchUser(userId);
        verify(mapper).toDto(testComment, testUsername);
    }

    @Test
    void throwImageNotFoundException_When_ImageDoesNotExist() {
        when(imageService.checkImageExists(imageId))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        assertThrows(ImageNotFoundException.class,
                () -> commentService.postNewComment(imageId, testNewCommentDto));
        verify(imageService).checkImageExists(imageId);
        verify(repository, never()).save(any());
    }

    @Test
    void updateComment_When_UserIsOwner() {
        when(repository.findByIdAuthorized(commentId, userId)).thenReturn(testComment);
        when(repository.save(testComment)).thenReturn(testComment);
        when(userServiceClient.fetchUser(userId)).thenReturn(Optional.of(testUserDto));
        when(mapper.toDto(testComment, testUsername)).thenReturn(testCommentDto);

        CommentDto result = commentService.updateComment(commentId, testNewCommentDto);

        assertNotNull(result);
        assertEquals(content, result.content());
        verify(repository).findByIdAuthorized(commentId, userId);
        verify(repository).save(testComment);
        verify(userServiceClient).fetchUser(userId);
    }

    @Test
    void throwNonAuthorizedCommentAccessException_When_UserIsNotOwner() {
        when(repository.findByIdAuthorized(commentId, userId))
                .thenThrow(new NonAuthorizedCommentAccessException(userId, commentId));

        assertThrows(NonAuthorizedCommentAccessException.class,
                () -> commentService.updateComment(commentId, testNewCommentDto));

        verify(repository).findByIdAuthorized(commentId, userId);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteComment_When_UserIsOwner() {
        when(repository.findByIdAuthorized(commentId, userId)).thenReturn(testComment);

        commentService.deleteComment(commentId);

        verify(repository).findByIdAuthorized(commentId, userId);
        verify(repository).deleteById(commentId);
    }

    @Test
    void returnPageOfCommentDtos_When_ValidDataProvided() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Comment> commentPage = new PageImpl<>(List.of(testComment));
        Page<CommentDto> commentDtoPage = new PageImpl<>(List.of(testCommentDto));

        when(imageService.checkImageExists(imageId)).thenReturn(ResponseEntity.ok().build());
        when(repository.findCommentsByImageId(imageId, pageable)).thenReturn(commentPage);
        when(userServiceClient.fetchUser(userId)).thenReturn(Optional.of(testUserDto));
        when(mapper.toDto(testComment, testUsername)).thenReturn(testCommentDto);

        Page<CommentDto> result = commentService.getImageComments(imageId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(commentDtoPage.getContent(), result.getContent());
        verify(imageService).checkImageExists(imageId);
        verify(repository).findCommentsByImageId(imageId, pageable);
        verify(userServiceClient).fetchUser(userId);
    }

    @Test
    void deleteCommentsByImageId_When_Called() {
        commentService.deleteImageComments(imageId);
        verify(repository).deleteCommentsByImageId(imageId);
    }

    @Test
    void returnCurrentUserId_When_AuthenticationIsSet() {
        Long result = commentService.currentUserId();
        assertEquals(userId, result);
    }

    @Test
    void throwNonAuthorizedCommentAccessException_When_DeletingCommentUserIsNotOwner() {
        when(repository.findByIdAuthorized(commentId, userId))
                .thenThrow(new NonAuthorizedCommentAccessException(userId, commentId));

        assertThrows(NonAuthorizedCommentAccessException.class,
                () -> commentService.deleteComment(commentId));

        verify(repository).findByIdAuthorized(commentId, userId);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void throwImageNotFoundException_When_GettingCommentsForNonExistentImage() {
        Pageable pageable = Pageable.ofSize(10);

        when(imageService.checkImageExists(imageId))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        assertThrows(ImageNotFoundException.class,
                () -> commentService.getImageComments(imageId, pageable));

        verify(imageService).checkImageExists(imageId);
        verify(repository, never()).findCommentsByImageId(anyLong(), any(Pageable.class));
    }

}
