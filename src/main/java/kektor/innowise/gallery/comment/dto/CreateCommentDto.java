package kektor.innowise.gallery.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CreateCommentDto",
        description = "Request model for creating a new comment"
)
public record CreateCommentDto(

        @Schema(
                description = "Comment text content",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Test comment content",
                maxLength = 500
        )
        @NotBlank
        @Size(max = 500)
        String content

) {
}

