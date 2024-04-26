package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.comment.CommentCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.CommentRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.PostRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository repository;

    @AfterEach
    public void cleanup() {
        repository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
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

    private static final String THIRD_VALID_USER_JSON = """
            {
                "email": "lucasgammmer999999@outlook.com",
                "username": "Terceiro",
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

    private static final String THIRD_VALID_LOGIN_JSON = """
            {
                "username": "Terceiro",
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

    public void loginWithFirstUserActivated() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        UUID id = objectMapper.readValue(register.getContentAsString(), UserDetailDTO.class).id();
        mvc.perform(get("/users/activate/{uuid}", id));
        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        token = dto.token();
    }

    public void loginWithSecondUserActivated() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        UUID id = objectMapper.readValue(register.getContentAsString(), UserDetailDTO.class).id();
        mvc.perform(get("/users/activate/{uuid}", id));
        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_LOGIN_JSON)).andReturn().getResponse();
        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        token = dto.token();
    }

    public void loginWithUserNotActivated() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(THIRD_VALID_USER_JSON));
        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(THIRD_VALID_LOGIN_JSON)).andReturn().getResponse();
        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        token = dto.token();
    }

    // Post Comment

    @Test
    public void commentPost_whenCommentIsValidAndUserIsAuthenticated_receiveCreatedAndPostCommentsSize1() throws Exception {
        loginWithFirstUserActivated();

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
        loginWithFirstUserActivated();

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
        loginWithFirstUserActivated();

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
    public void commentPost_whenUserIsUnauthorized_receiveForbiddenAndPostCommentsSize0() throws Exception {
        loginWithFirstUserActivated();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void commentPost_whenUserIsAuthorizedButNotActivated_receiveForbiddenAndPostCommentsSize0() throws Exception {
        loginWithFirstUserActivated();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        loginWithUserNotActivated();
        var response = mvc.perform(post("/posts/post/{uuid}/comments", idPost)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    public void commentPost_whenUserIsAuthorizedButPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(post("/posts/post/{uuid}/comments", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Patch Comment

    @Test
    public void patchComment_whenTextIsValidAndUserIsAuthenticated_receiveAccepted() throws Exception {
        loginWithFirstUserActivated();

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
        loginWithFirstUserActivated();

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
        loginWithFirstUserActivated();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithSecondUserActivated();
        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void patchComment_whenTextIsValidButUserIsUnauthorized_receiveForbidden() throws Exception {
        loginWithFirstUserActivated();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void patchComment_whenTextIsValidButUserIsAuthorizedButNotActivated_receiveForbidden() throws Exception {
        loginWithFirstUserActivated();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithUserNotActivated();
        var response = mvc.perform(patch("/posts/post/comments/{uuid}", idComment)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void patchComment_whenTextIsValidButCommentNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(patch("/posts/post/comments/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_COMMENT)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Delete Comment

    @Test
    public void deleteComment_whenUserIsAuthenticated_receiveNoContent() throws Exception {
        loginWithFirstUserActivated();

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
        loginWithFirstUserActivated();
        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithSecondUserActivated();
        var response = mvc.perform(delete("/posts/post/comments/{uuid}", idComment)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(1);
    }

    @Test
    public void deleteComment_whenUserIsUnauthenticated_receiveForbidden() throws Exception {
        loginWithFirstUserActivated();
        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        var response = mvc.perform(delete("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(1);
    }

    @Test
    public void deleteComment_whenUserIsAuthenticatedButNotActivated_receiveForbidden() throws Exception {
        loginWithFirstUserActivated();
        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        UUID idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();

        loginWithUserNotActivated();
        var response = mvc.perform(delete("/posts/post/comments/{uuid}", idComment)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var detail = mvc.perform(get("/posts/post/{uuid}", idPost)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        int size = objectMapper.readValue(detail.getContentAsString(), PostDetailDTO.class).comments().size();

        assertThat(size).isEqualTo(1);
    }

    @Test
    public void deleteComment_whenUserIsAuthenticatedButCommentNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(delete("/posts/post/comments/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}