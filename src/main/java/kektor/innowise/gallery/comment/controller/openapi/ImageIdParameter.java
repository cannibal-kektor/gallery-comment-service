package kektor.innowise.gallery.comment.controller.openapi;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@Parameter(
        name = "imageId",
        description = "Unique image id",
        in = ParameterIn.PATH,
        example = "12345",
        schema = @Schema(type = "integer", format = "int64", minimum = "1")
)
public @interface ImageIdParameter {
}