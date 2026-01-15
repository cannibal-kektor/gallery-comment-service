package kektor.innowise.gallery.comment.repository;


import kektor.innowise.gallery.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long>, UtilityRepositoryFragment {

    Page<Comment> findCommentsByImageId(Long imageId, Pageable pageable);

    void deleteCommentsByImageId(Long imageId);

}
