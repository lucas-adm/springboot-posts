package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.comment.CommentCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository repository;

    @BeforeEach
    public void cleanup() {
        repository.deleteAll();
    }

    private static final String VALID_USER_JSON = """
            {
                "email": "lucasgammmer123456@outlook.com",
                "username": "Lucas",
                "password": "Senha123",
                "birthDate": "2002-06-22"
            }
            """;

    private static final String SECOND_VALID_USER_JSON = """
            {
                "email": "lucasgammmer456789@outlook.com",
                "username": "Segundo Lucas",
                "password": "Senha123",
                "birthDate": "2002-06-22"
            }
            """;

    private static final String VALID_LOGIN_JSON = """
            {
                "username": "Lucas",
                "password": "Senha123"
            }
            """;

    private static final String SECOND_VALID_LOGIN_JSON = """
            {
                "username": "Segundo Lucas",
                "password": "Senha123"
            }
            """;

    private static final String VALID_POST = """
            {
                "text": "Post"
            }
            """;

    private static final String VALID_COMMENT = """
            {
            	"text": "simple comment"
            }
            """;

    private static final String INVALID_COMMENT = """
            {
            	"texto": "simple comment"
            }
            """;

    private static final String VALID_PATCH_COMMENT = """
            {
            	"text": "new comment version"
            }
            """;

    private static final String INVALID_PATCH_COMMENT = """
            {
            	"texto": "new comment version"
            }
            """;

    private static final UUID INVALID_ID = UUID.randomUUID();

    private String token;

    // Register and Login

    public void loginWithFirstUser() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));
        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        token = dto.token();
    }

    public void loginWithSecondUser() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON));
        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_LOGIN_JSON)).andReturn().getResponse();
        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        token = dto.token();
    }

    // Post Comment

    @Test
    public void commentPost_whenCommentIsValidAndUserIsAuthenticated_receiveCreatedAndPostCommentsSize1() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(1);
    }

    @Test
    public void commentPost_whenCommentIsInvalid_receiveBadRequestAndPostCommentsSize0() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void commentPost_whenCommentIsValidAndUserIsAuthenticatedButPostIsClosed_receiveBadRequestAndPostCommentsSize0() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        mvc.perform(patch("/posts/post/{uuid}", idPost).header("Authorization", "Bearer " + token));

        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void commentPost_whenUserIsUnauthorized_receiveBadRequestAndPostCommentsSize0() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void commentPost_whenUserIsAuthorizedButPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUser();
        var response = mvc.perform(post("/posts/post/{uuid}/comments", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Patch Comment

    @Test
    public void patchComment_whenTextIsValidAndUserIsAuthenticated_receiveAccepted() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
    }

    @Test
    public void patchComment_whenTextIsInvalidAndUserIsAuthenticated_receiveBadRequest() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void patchComment_whenTextIsValidAndUserIsAuthenticatedButNotCommentCreator_receiveUnauthorized() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithSecondUser();
        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void patchComment_whenTextIsValidButUserIsUnauthenticated_receiveBadRequest() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void patchComment_whenTextIsValidButCommentNotExists_receiveNotFound() throws Exception {
        loginWithFirstUser();
        var response = mvc.perform(patch("/posts/post/comments/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Delete Comment

    @Test
    public void deleteComment_whenUserIsAuthenticated_receiveNoContent() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(delete("/posts/post/comments/{uuid}", idComment)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void deleteComment_whenUserIsAuthenticatedButNotCommentCreator_receiveUnauthorized() throws Exception {
        loginWithFirstUser();
        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithSecondUser();
        var response = mvc.perform(delete("/posts/post/comments/{uuid}", idComment)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(1);
    }

    @Test
    public void deleteComment_whenUserIsAuthenticatedButCommentNotExists_receiveNotFound() throws Exception {
        loginWithFirstUser();
        var response = mvc.perform(delete("/posts/post/comments/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}