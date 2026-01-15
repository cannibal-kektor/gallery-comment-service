package kektor.innowise.gallery.comment.conf;


import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Comment Service API - Image Gallery Microservices",
                version = "${app.version}",
                description = """
                        Comment Management Microservice for the Image Gallery application.
                        This service handles comment creation, retrieval, updating, and deletion for images.
                        """,
                contact = @Contact(
                        name = "cannibal-kektor",
                        url = "https://github.com/cannibal-kektor"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        security = {
                @SecurityRequirement(name = OpenApiConfig.JWT_BEARER_TOKEN),
                @SecurityRequirement(name = OpenApiConfig.INTERNAL_SERVICE_AUTH)
        }
)
@SecurityScheme(
        name = OpenApiConfig.JWT_BEARER_TOKEN,
        description = """
                JWT authentication token obtained from the Authentication Service.
                The API Gateway validates the token and forwards user information in headers:
                - X-User-Id: User identifier
                - X-User-Email: User email address
                - X-User-Username: User username
                """,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityScheme(
        name = OpenApiConfig.INTERNAL_SERVICE_AUTH,
        description = """
                Internal service authentication for inter-service communication.
                Requires the X-System-Internal-Call header with the origin service name.
                Used for service-to-service calls within the microservices ecosystem.
                """,
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-System-Internal-Call"
)
public class OpenApiConfig {

    public static final String INTERNAL_SERVICE_AUTH = "internal-service";
    public static final String JWT_BEARER_TOKEN = "bearer-token";
    public static final String PROBLEM_DETAIL = "ProblemDetail";
    public static final String PROBLEM_DETAIL_RESPONSE = "ProblemDetailResponse";

    @Bean
    public OpenApiCustomizer commonErrorResponseOpenApiCustomizer() {
        MediaType mediaType = new MediaType().schema(new Schema<>().$ref(PROBLEM_DETAIL));
        Content content = new Content().addMediaType("application/problem+json", mediaType);

        //register ProblemDetail schema
        Schema<?> problemDetailSchema = ModelConverters.getInstance()
                .read(ProblemDetail.class)
                .get(PROBLEM_DETAIL);

        return openApi -> openApi.getComponents()
                .addSchemas(PROBLEM_DETAIL, problemDetailSchema)
                .addResponses(PROBLEM_DETAIL_RESPONSE,
                        new ApiResponse()
                                .description("RFC 7807 Error response")
                                .content(content)
                );
    }

}
