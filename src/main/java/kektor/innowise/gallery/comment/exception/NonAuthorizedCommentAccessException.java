package kektor.innowise.gallery.comment.exception;

public class NonAuthorizedCommentAccessException extends RuntimeException {

    private static final String AUTHORIZATION_OWNER_ACCESS_EXCEPTION = "User: (id=%d) is not the owner of the accessed comment(id=%d)";

    public NonAuthorizedCommentAccessException(Long userId, Long commentId) {
        super(String.format(AUTHORIZATION_OWNER_ACCESS_EXCEPTION, userId, commentId));
    }
}
