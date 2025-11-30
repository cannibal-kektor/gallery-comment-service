package kektor.innowise.gallery.comment.controller;

import jakarta.validation.Valid;
import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    final CommentService commentService;

    @GetMapping(
            path = "/{commentId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommentDto> get(@PathVariable Long commentId) {
        return ResponseEntity.ok()
                .body(commentService.getById(commentId));
    }

    @PostMapping(
            value = "/image/{imageId}/post",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommentDto> postNewComment(@PathVariable Long imageId,
                                                     @Valid @RequestBody CreateCommentDto newComment) {
        return ResponseEntity.ok()
                .body(commentService.postNewComment(imageId, newComment));
    }

    @PutMapping(
            value = "/{commentId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId,
                                             @Valid @RequestBody CreateCommentDto newComment) {
        return ResponseEntity.ok()
                .body(commentService.updateComment(commentId, newComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(
            path = "/image/{imageId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CommentDto>> getImageComments(@PathVariable Long imageId,
                                                             @PageableDefault(sort = "createdAt",
                                                                     direction = Sort.Direction.DESC)
                                                             Pageable pageable) {
        return ResponseEntity.ok()
                .body(commentService.getImageComments(imageId, pageable));
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<Void> deleteImageComments(@PathVariable Long imageId) {
        commentService.deleteImageComments(imageId);
        return ResponseEntity.ok().build();
    }

}
