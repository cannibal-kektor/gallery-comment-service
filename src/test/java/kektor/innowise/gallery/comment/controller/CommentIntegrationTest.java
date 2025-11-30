package kektor.innowise.gallery.comment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import kektor.innowise.gallery.comment.dto.CreateCommentDto;
import kektor.innowise.gallery.comment.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static kektor.innowise.gallery.security.HeadersAuthenticationFilter.EMAIL_HEADER;
import static kektor.innowise.gallery.security.HeadersAuthenticationFilter.USERNAME_HEADER;
import static kektor.innowise.gallery.security.HeadersAuthenticationFilter.USER_ID_HEADER;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(port = 8089)
})
@Sql(scripts = {
        "/sql/cleanup.sql",
        "/sql/test-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
public class CommentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("comments_db")
            .withUsername("testUser")
            .withPassword("testPassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("gallery.security.protected-services.user-service-url", () -> "http://localhost:8089");
        registry.add("gallery.security.protected-services.image-service-url", () -> "http://localhost:8089");
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    HttpHeaders headers;
    UserDto user;

    @BeforeEach
    void setUp() {
        headers = createAuthHeaders(1L, "user1", "user1@test.com");
        user = new UserDto(1L, "testUsername", "testEmail");
    }

    @AfterEach
    void clear() {
        WireMock.resetToDefault();
    }

    HttpHeaders createAuthHeaders(Long userId, String username, String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(USER_ID_HEADER, userId.toString());
        headers.add(USERNAME_HEADER, username);
        headers.add(EMAIL_HEADER, email);
        return headers;
    }

    @Test
    void returnComment_When_ValidCommentIdProvided() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/api/users/id/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(user))
                        .withStatus(200)));

        mockMvc.perform(get("/api/comments/1")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.imageId").value(1))
                .andExpect(jsonPath("$.content").value("test comment 1"))
                .andExpect(jsonPath("$.username").value("testUsername"));
    }

    @Test
    void returnNotFound_When_CommentDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/comments/100000")
                        .headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewComment_When_ValidDataProvided() throws Exception {
        CreateCommentDto newComment = new CreateCommentDto("New test comment");

        stubFor(head(urlPathEqualTo("/api/images/1"))
                .willReturn(aResponse().withStatus(200)));
        stubFor(WireMock.get(urlPathEqualTo("/api/users/id/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(user))
                        .withStatus(200)));

        mockMvc.perform(post("/api/comments/image/1/post")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.imageId").value(1))
                .andExpect(jsonPath("$.content").value("New test comment"))
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }


    @Test
    void returnNotFound_When_ImageDoesNotExist() throws Exception {
        CreateCommentDto newComment = new CreateCommentDto("New test comment");

        stubFor(head(urlPathEqualTo("/api/images/100000"))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post("/api/comments/image/100000/post")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnBadRequest_When_ContentIsBlank() throws Exception {
        CreateCommentDto newComment = new CreateCommentDto("   ");

        mockMvc.perform(post("/api/comments/image/1/post")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnBadRequest_When_ContentExceedsMaxLength() throws Exception {
        String longContent = "a".repeat(501);
        CreateCommentDto newComment = new CreateCommentDto(longContent);

        mockMvc.perform(post("/api/comments/image/1/post")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_When_UserIsOwner() throws Exception {
        String updatedCommentContent = "Updated comment content";
        CreateCommentDto updatedComment = new CreateCommentDto(updatedCommentContent);

        stubFor(WireMock.get(urlPathEqualTo("/api/users/id/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(user))
                        .withStatus(200)));

        mockMvc.perform(put("/api/comments/1")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.imageId").value(1))
                .andExpect(jsonPath("$.content").value(updatedCommentContent))
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());


        mockMvc.perform(get("/api/comments/1")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updatedCommentContent));
    }

    @Test
    void returnForbidden_When_UserIsNotOwnerTryingToUpdate() throws Exception {
        HttpHeaders headers = createAuthHeaders(2L, "user2", "user2@test.com");
        CreateCommentDto updatedComment = new CreateCommentDto("Unauthorized update");

        mockMvc.perform(put("/api/comments/1")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_When_UserIsOwner() throws Exception {
        mockMvc.perform(delete("/api/comments/1")
                        .headers(headers))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/comments/1")
                        .headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnForbidden_When_UserIsNotOwnerTryingToDelete() throws Exception {
        HttpHeaders headers = createAuthHeaders(2L, "user2", "user2@test.com");

        mockMvc.perform(delete("/api/comments/1")
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnPaginatedComments_When_ValidImageIdProvided() throws Exception {
        stubFor(head(urlPathEqualTo("/api/images/1"))
                .willReturn(aResponse().withStatus(200)));
        stubFor(WireMock.get(urlPathEqualTo("/api/users/id/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(user))
                        .withStatus(200)));
        UserDto secondUser = new UserDto(2L, "testUsername2", "testEmail2");
        stubFor(WireMock.get(urlPathEqualTo("/api/users/id/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(secondUser))
                        .withStatus(200)));

        mockMvc.perform(get("/api/comments/image/1?page=0&size=10&sort=createdAt,desc")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].imageId").value(1))
                .andExpect(jsonPath("$.content[1].imageId").value(1))
                .andExpect(jsonPath("$.content[0].userId").value(2))
                .andExpect(jsonPath("$.content[0].username").value("testUsername2"))
                .andExpect(jsonPath("$.content[1].userId").value(1))
                .andExpect(jsonPath("$.content[1].username").value("testUsername"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void returnEmptyPage_When_ImageHasNoComments() throws Exception {

        stubFor(head(urlPathEqualTo("/api/images/100"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(get("/api/comments/image/100?page=0&size=10")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void deleteAllImageComments_When_Requested() throws Exception {
        mockMvc.perform(delete("/api/comments/image/1")
                        .headers(headers))
                .andExpect(status().isOk());

        stubFor(head(urlPathEqualTo("/api/images/1"))
                .willReturn(aResponse().withStatus(200)));

        mockMvc.perform(get("/api/comments/image/1?page=0&size=10")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

}
