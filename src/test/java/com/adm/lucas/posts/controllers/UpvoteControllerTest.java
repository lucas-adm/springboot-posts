package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.UpvoteRepository;
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
@ActiveProfiles("test")
@SpringBootTest
public class UpvoteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UpvoteRepository repository;

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

    // Post Upvote

    @Test
    public void upvotePost_whenPostExistsAndUserIsAuthorized_receiveOkAndPostUpvoteSize1() throws Exception {
        loginWithFirstUser();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID id = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        var post = mvc.perform(get("/posts/post/{uuid}", id)).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(detail.upvotes()).isEqualTo(1);
    }

    @Test
    public void upvotePost_whenUserAlreadyDidUpvote_receiveBadRequestAndPostUpvoteSize1() throws Exception {
        loginWithFirstUser();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID id = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class).id();

        var first = mvc.perform(post("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(first.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        var second = mvc.perform(post("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(second.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var post = mvc.perform(get("/posts/post/{uuid}", id)).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(detail.upvotes()).isEqualTo(1);
    }

    @Test
    public void upvotePost_whenPostExistsAndUserIsUnauthorized_receiveBadRequestAndPostUpvoteSize0() throws Exception {
        loginWithFirstUser();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID id = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/upvotes/{uuid}", id));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var post = mvc.perform(get("/posts/post/{uuid}", id)).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(detail.upvotes()).isEqualTo(0);
    }

    @Test
    public void upvotePost_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUser();
        var response = mvc.perform(post("/posts/post/upvotes/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // Delete Upvote

    @Test
    public void deleteUpvote_whenPostExistsAndUserIsAuthorized_receiveNoContentAndPostUpvoteSize1() throws Exception {
        loginWithFirstUser();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID id = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class).id();

        var first = mvc.perform(post("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(first.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        loginWithSecondUser();
        var second = mvc.perform(post("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(second.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        var response = mvc.perform(delete("/posts/post/upvotes/{uuid}", id).header("Authorization", "Bearer " + token));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        var post = mvc.perform(get("/posts/post/{uuid}", id)).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(detail.upvotes()).isEqualTo(1);
    }

    @Test
    public void deleteUpvote_whenPostExistsAndUserIsUnauthorized_receiveBadRequestAndPostUpvoteSize0() throws Exception {
        loginWithFirstUser();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        UUID id = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class).id();

        var response = mvc.perform(post("/posts/post/upvotes/{uuid}", id));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var post = mvc.perform(get("/posts/post/{uuid}", id)).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(detail.upvotes()).isEqualTo(0);
    }

    @Test
    public void deleteUpvote_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUser();
        var response = mvc.perform(delete("/posts/post/upvotes/{uuid}", INVALID_ID).header("Authorization", "Bearer " + token));
        assertThat(response.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}