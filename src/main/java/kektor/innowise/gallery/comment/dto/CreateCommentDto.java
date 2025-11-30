package kektor.innowise.gallery.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentDto(
        @NotBlank
        @Size(max = 500)
        String content
) {
}

