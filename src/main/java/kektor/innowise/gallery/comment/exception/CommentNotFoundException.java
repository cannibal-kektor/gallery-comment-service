package kektor.innowise.gallery.comment.exception;

public class CommentNotFoundException extends RuntimeException {

    private static final String COMMENT_NOT_FOUND = "Comment with id: (%d) not found";

    public CommentNotFoundException(Long commentId) {
        super(String.format(COMMENT_NOT_FOUND, commentId));
    }
}
