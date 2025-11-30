package kektor.innowise.gallery.comment.dto;

import java.time.Instant;

public record CommentDto(
        Long id,
        Long userId,
        String username,
        Long imageId,
        String content,
        Instant createdAt
) {
}
