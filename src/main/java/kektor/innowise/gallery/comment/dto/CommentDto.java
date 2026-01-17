package kektor.innowise.gallery.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "CommentDto",
        description = "Comment data model containing comment information"
)
public record CommentDto(

        @Schema(
                description = "Unique comment id",
                example = "12345",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "User identifier who created the comment",
                example = "67890",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long userId,

        @Schema(
                description = "Username of the comment author",
                example = "alex_white",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String username,

        @Schema(
                description = "Image id the comment belongs to",
                example = "54321",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long imageId,

        @Schema(
                description = "Comment content text",
                example = "Test comment content",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String content,

        @Schema(
                description = "Comment creation timestamp",
                example = "2025-11-01T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant createdAt
) {
}
