package kektor.innowise.gallery.comment.repository;

import kektor.innowise.gallery.comment.model.Comment;

public interface UtilityRepositoryFragment {

    Comment findByIdExceptionally(Long commentId);

    Comment findByIdAuthorized(Long commentId, Long userId);

}
