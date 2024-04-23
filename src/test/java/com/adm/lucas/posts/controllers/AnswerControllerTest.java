package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.answer.AnswerDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.comment.CommentCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.AnswerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
public class AnswerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnswerRepository repository;

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

    private static final String VALID_ANSWER = """
            {
            	"text": "simple answer"
            }
            """;

    private static final String INVALID_ANSWER = """
            {
            	"texto": "simple answer"
            }
            """;

    private static final String VALID_PATCH_ANSWER = """
            {
            	"text": "simple new answer"
            }
            """;

    private static final String INVALID_PATCH_ANSWER = """
            {
            	"texto": "simple new answer"
            }
            """;

    private static final UUID INVALID_ID = UUID.randomUUID();

    private String token;
    private UUID idPost;
    private UUID idComment;

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

    @BeforeEach
    public void setup() throws Exception {
        loginWithFirstUser();

        var post = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        idPost = objectMapper.readValue(post.getContentAsString(), PostCreatedDTO.class).id();

        var comment = mvc.perform(post("/posts/post/{uuid}/comments", idPost).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_COMMENT)).andReturn().getResponse();
        idComment = objectMapper.readValue(comment.getContentAsString(), CommentCreatedDTO.class).id();
    }

    // Post Answer

    @Test
    public void answerComment_whenTextIsValidAndUserIsAuthenticated_receiveCreatedAndAnswersSize1() throws Exception {
        var response = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(1);
    }

    @Test
    public void answerComment_whenTextIsInvalidAndUserIsAuthenticated_receiveBadRequestAndAnswersSize0() throws Exception {
        var response = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_ANSWER)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(0);
    }

    @Test
    public void answerComment_whenTextIsValidButUserIsNotAuthenticated_receiveBadRequestAndAnswersSize0() throws Exception {
        var response = mvc.perform(post("/posts/post/comments/{uuid}", idComment)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(0);
    }

    @Test
    public void answerComment_whenTextIsValidButCommentNotExists_receiveNotFound() throws Exception {
        var response = mvc.perform(post("/posts/post/comments/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Patch

    @Test
    public void editAnswer_whenTextIsValidAndUserIsAuthenticated_receiveOk() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/answers/{uuid}", idAnswer).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_ANSWER)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
    }

    @Test
    public void editAnswer_whenTextIsInvalidAndUserIsAuthenticated_receiveBadRequest() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        var response = mvc.perform(patch("/posts/post/comments/answers/{uuid}", idAnswer).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_PATCH_ANSWER)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void editAnswer_whenTextIsValidAndUserIsAuthenticatedButNotAnswerCreator_receiveUnauthorized() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        loginWithSecondUser();
        var response = mvc.perform(patch("/posts/post/comments/answers/{uuid}", idAnswer).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_ANSWER)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void editAnswer_whenTextIsValidAndUserIsAuthenticatedButCommentNotExists_receiveNotFound() throws Exception {
        var response = mvc.perform(patch("/posts/post/comments/answers/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PATCH_ANSWER)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Delete

    @Test
    public void deleteAnswer_whenUserIsAuthenticated_receiveNoContent() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        var response = mvc.perform(delete("/posts/post/comments/answers/{uuid}", idAnswer)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(0);
    }

    @Test
    public void deleteAnswer_whenUserIsNotAuthenticated_receiveBadRequest() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        var response = mvc.perform(delete("/posts/post/comments/answers/{uuid}", idAnswer)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(1);
    }

    @Test
    public void deleteAnswer_whenUserIsAuthenticatedButNotAnswerCreator_receiveUnauthorized() throws Exception {
        var answer = mvc.perform(post("/posts/post/comments/{uuid}", idComment).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_ANSWER)).andReturn().getResponse();
        UUID idAnswer = objectMapper.readValue(answer.getContentAsString(), AnswerDetailDTO.class).id();

        loginWithSecondUser();
        var response = mvc.perform(delete("/posts/post/comments/answers/{uuid}", idAnswer)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var page = mvc.perform(get("/posts/post/comments/{uuid}", idComment)).andReturn().getResponse();
        List<AnswerDetailDTO> answers = objectMapper.readValue(page.getContentAsString(), new TypeReference<List<AnswerDetailDTO>>() {
        });
        assertThat(answers.size()).isEqualTo(1);
    }

    @Test
    public void deleteAnswer_whenUserIsAuthenticatedButAnswerNotExists_receiveNotFound() throws Exception {
        var response = mvc.perform(delete("/posts/post/comments/answers/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}