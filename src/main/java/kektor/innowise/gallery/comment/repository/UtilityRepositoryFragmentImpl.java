package kektor.innowise.gallery.comment.repository;


import kektor.innowise.gallery.comment.exception.CommentNotFoundException;
import kektor.innowise.gallery.comment.exception.NonAuthorizedCommentAccessException;
import kektor.innowise.gallery.comment.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UtilityRepositoryFragmentImpl implements UtilityRepositoryFragment {

    private final JdbcAggregateTemplate template;

    @Override
    public Comment findByIdExceptionally(Long commentId) {
        Comment comment = template.findById(commentId, Comment.class);
        if (comment == null) {
            throw new CommentNotFoundException(commentId);
        }
        return comment;
    }

    @Override
    public Comment findByIdAuthorized(Long commentId, Long userId) {
        Comment comment = findByIdExceptionally(commentId);
        if (!userId.equals(comment.getUserId())) {
            throw new NonAuthorizedCommentAccessException(userId, commentId);
        }
        return comment;
    }
}
