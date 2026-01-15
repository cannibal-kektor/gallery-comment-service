package kektor.innowise.gallery.comment.controller.openapi;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.FailedApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kektor.innowise.gallery.comment.dto.CommentDto;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static kektor.innowise.gallery.comment.conf.OpenApiConfig.INTERNAL_SERVICE_AUTH;
import static kektor.innowise.gallery.comment.conf.OpenApiConfig.JWT_BEARER_TOKEN;
import static kektor.innowise.gallery.comment.conf.OpenApiConfig.PROBLEM_DETAIL_RESPONSE;

@Tag(
        name = "Comment Management API",
        description = "API for managing comments on images in the Image Gallery system. "
)
@FailedApiResponse(ref = PROBLEM_DETAIL_RESPONSE)
public interface CommentServiceOpenApi {

    @Operation(
            summary = "Get comment by ID",
            description = "Retrieves a specific comment by its unique id",
            security = @SecurityRequirement(name = JWT_BEARER_TOKEN)

    )
    @CommentDtoResponse
    ResponseEntity<CommentDto> get(@CommentIdParameter Long commentId);

    @Operation(
            summary = "Create new comment on image",
            description = "Creates a new comment on the specified image.",
            requestBody = @RequestBody(
                    description = "Comment content to create",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateCommentDto.class)
                    )
            ),
            security = @SecurityRequirement(name = JWT_BEARER_TOKEN)
    )
    @CommentDtoResponse
    ResponseEntity<CommentDto> postNewComment(@ImageIdParameter Long imageId,
                                              @Valid CreateCommentDto newComment);

    @Operation(
            summary = "Update existing comment",
            description = "Updates the content of an existing comment.",
            requestBody = @RequestBody(
                    description = "Updated comment content",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateCommentDto.class)
                    )
            ),
            security = @SecurityRequirement(name = JWT_BEARER_TOKEN)
    )
    @CommentDtoResponse
    ResponseEntity<CommentDto> update(@CommentIdParameter Long commentId,
                                      @Valid CreateCommentDto newComment);

    @Operation(
            summary = "Delete comment",
            description = "Deletes a specific comment",
            security = @SecurityRequirement(name = JWT_BEARER_TOKEN)
    )
    @ApiResponse(
            responseCode = "200",
            description = "Comment deleted successfully"
    )
    ResponseEntity<Void> delete(@CommentIdParameter Long commentId);

    @Operation(
            summary = "Get image comments",
            description = "Retrieves paginated comments of the specific image.",
            security = @SecurityRequirement(name = JWT_BEARER_TOKEN)
    )
    @ApiResponse(
            responseCode = "200",
            description = "Comments retrieved successfully"
    )
    ResponseEntity<Page<CommentDto>> getImageComments(@ImageIdParameter Long imageId,
                                                      @ParameterObject Pageable pageable);

    @Operation(
            summary = "Delete all comments for image",
            description = "Deletes all comments associated with a specific image",
            security = @SecurityRequirement(name = INTERNAL_SERVICE_AUTH)
    )
    @ApiResponse(
            responseCode = "200",
            description = "All image comments deleted successfully"
    )
    ResponseEntity<Void> deleteImageComments(@ImageIdParameter Long imageId);
}
