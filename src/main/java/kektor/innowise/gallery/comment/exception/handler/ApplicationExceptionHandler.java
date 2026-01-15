package kektor.innowise.gallery.comment.exception.handler;

import kektor.innowise.gallery.comment.exception.CommentNotFoundException;
import kektor.innowise.gallery.comment.exception.ImageNotFoundException;
import kektor.innowise.gallery.comment.exception.NonAuthorizedCommentAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;


@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ImageNotFoundException.class, CommentNotFoundException.class})
    public ErrorResponse handleNotFound(Exception ex) {
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NonAuthorizedCommentAccessException.class)
    public ErrorResponse handleNonAuthorizedImageAccess(NonAuthorizedCommentAccessException ex) {
        return ErrorResponse.create(ex, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ProblemDetail> handleRestClientResponseException(RestClientResponseException ex) {
        ProblemDetail detail = ex.getResponseBodyAs(ProblemDetail.class);
        return ResponseEntity.status(ex.getStatusCode()).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAll(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, Optional.ofNullable(ex.getMessage())
                .orElse("Internal server error"));
    }

}