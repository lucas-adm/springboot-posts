package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostsDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.PostRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.Status;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository repository;

    @AfterEach
    public void cleanup() {
        repository.deleteAll();
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

    private static final String INVALID_POST = """
            {
                "text": ""
            }
            """;

    private static final String INVALID_POST_EXCEEDED = """
            {
                "text": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In eu consectetur nunc. Duis aliquet ex magna, eu commodo urna ullamcorper eget. Nam mollis turpis eget mauris consequat egestas. Morbi at orci porttitor, luctus ex sit amet, ultrices felis. Curabitur luctus egestas eros, in condimentum neque luctus a. Vestibulum mauris neque, placerat vitae hendrerit vestibulum, laoreet sed augue. Cras scelerisque diam et scelerisque congue. Sed aliquam ultrices massa, non rutrum turpis imperdiet non. Sed id tellus quis lectus auctor suscipit faucibus nec ex. Nulla consequat libero ac ligula blandit, vitae consectetur velit venenatis. Fusce mollis blandit ullamcorper. Vestibulum luctus nunc vel ullamcorper commodo. Mauris hendrerit, elit ac suscipit luctus, mauris est luctus lectus, eget tincidunt justo diam sit amet quam. Nullam et leo vel leo finibus feugiat. Cras tempus ipsum ac faucibus iaculis. Nunc in eros et justo interdum malesuada. Praesent feugiat interdum placerat. Phasellus id odio vitae metus vestibulum consectetur sed et leo. Mauris sollicitudin hendrerit ipsum vitae molestie. Aenean elit purus, sollicitudin eu convallis a, condimentum non augue. Vestibulum elit elit, consectetur at elementum ut, porttitor vel justo. Suspendisse vehicula tellus sit amet tempus elementum. Mauris at facilisis velit, scelerisque fringilla mi. Donec semper libero sed diam consectetur lobortis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. In ullamcorper tellus ex, sit amet aliquam nisl gravida ac. Mauris sit amet orci ornare, condimentum felis molestie, cursus felis. Nulla massa elit, placerat et arcu vel, interdum scelerisque ex. Maecenas ut erat condimentum, convallis enim vitae, tempor eros. Vestibulum sed egestas neque."
            }
            """;

    private static final String VALID_EDIT_POST = """
            {
            	"text": "Post Editado"
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

    // Create

    @Test
    public void createPost_whenPostIsValid_receiveCreatedAndUserAndGetPost() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();

        var post = objectMapper.readValue(response.getContentAsString(), PostCreatedDTO.class).id();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var getPost = mvc.perform(get("/posts/{uuid}", post)).andReturn().getResponse();
        assertThat(getPost.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void createPost_whenPostIsInvalid_receiveBadRequestAndUserPostsSize0() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var list = mvc.perform(get("/posts/{username}", "Lucas")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });
        assertThat(posts.size()).isEqualTo(0);
    }

    @Test
    public void createPost_whenPostIsInvalidByExceeding_receiveBadRequestAndUserPostsSize0() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_POST_EXCEEDED)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var list = mvc.perform(get("/posts/{username}", "Lucas")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });
        assertThat(posts.size()).isEqualTo(0);
    }

    @Test
    public void createPost_whenPostIsValidButNotAuthorized_receiveForbiddenAndUserPostsSize0() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var list = mvc.perform(get("/posts/{username}", "Lucas")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });
        assertThat(posts.size()).isEqualTo(0);
    }

    @Test
    public void createPost_whenPostIsValidButUserIsNotActivated_receiveForbiddenAndUserPostsSize0() throws Exception {
        loginWithUserNotActivated();
        var response = mvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var list = mvc.perform(get("/posts/{username}", "Lucas")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });
        assertThat(posts.size()).isEqualTo(0);
    }

    // GET

    @Test
    public void getPost_whenPostExists_receivePost() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(dto.id()).isEqualTo(detail.id());
    }

    @Test
    public void getPost_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var post = mvc.perform(get("/posts/post/{uuid}", INVALID_ID)).andReturn().getResponse();
        assertThat(post.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAllPosts_receivePage() throws Exception {
        loginWithFirstUserActivated();
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));

        var list = mvc.perform(get("/posts?size=2")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });

        assertThat(list.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    public void getUserPosts_receivePage() throws Exception {
        loginWithFirstUserActivated();
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));
        mvc.perform(post("/posts").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(VALID_POST));

        var list = mvc.perform(get("/posts/{username}?size=2", "Lucas")).andReturn().getResponse();
        List<PostsDTO> posts = objectMapper.readValue(list.getContentAsString(), new TypeReference<List<PostsDTO>>() {
        });

        assertThat(list.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(posts.size()).isEqualTo(2);
    }


    // Edit

    @Test
    public void editPost_whenTextIsValid_receiveAcceptedAndGetNewText() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(put("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_EDIT_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        String text = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).text();

        assertThat(text).isEqualTo("Post Editado");
    }

    @Test
    public void editPost_whenTextIsInvalid_receiveBadRequestAndKeepsOldText() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(put("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        String text = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).text();

        assertThat(text).isEqualTo(dto.text());
    }

    @Test
    public void editPost_whenTextIsValidAndUserIsAuthorizedButNotPostOwner_receiveUnauthorizedAndKeepsOldText() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithSecondUserActivated();
        var response = mvc.perform(put("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_EDIT_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        String text = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).text();

        assertThat(text).isEqualTo(dto.text());
    }

    @Test
    public void editPost_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(put("/posts/post/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_EDIT_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void editPost_whenUserIsNotAuthorized_receiveForbiddenAndKeepsStatus() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(put("/posts/post/{uuid}", dto.id())
                .contentType(MediaType.APPLICATION_JSON).content(VALID_EDIT_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        String text = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).text();

        assertThat(text).isEqualTo(dto.text());
    }

    @Test
    public void editPost_whenUserIsAuthorizedButNotActivated_receiveForbiddenAndKeepsStatus() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithUserNotActivated();
        var response = mvc.perform(put("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_EDIT_POST)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        String text = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).text();

        assertThat(text).isEqualTo(dto.text());
    }

    // Patch

    @Test
    public void patchPost_whenUserIsAuthorizedAndPostCreator_receiveAcceptedAndStatusClosed() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(patch("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        Status status = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).status();

        assertThat(status).isEqualTo(Status.CLOSED);
    }

    @Test
    public void patchPost_whenUserIsAuthorizedButNotPostCreator_receiveUnauthorizedAndKeepsStatus() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithSecondUserActivated();
        var response = mvc.perform(patch("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        Status status = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).status();

        assertThat(status).isEqualTo(dto.status());
    }

    @Test
    public void patchPost_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(patch("/posts/post/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void patchPost_whenUserIsNotAuthorized_receiveForbiddenAndKeepsStatus() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(patch("/posts/post/{uuid}", dto.id())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        Status status = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).status();

        assertThat(status).isEqualTo(dto.status());
    }

    @Test
    public void patchPost_whenUserIsAuthorizedButNotActivated_receiveForbiddenAndKeepsStatus() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithUserNotActivated();
        var response = mvc.perform(patch("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        Status status = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class).status();

        assertThat(status).isEqualTo(dto.status());
    }

    // Delete

    @Test
    public void deletePost_whenUserIsAuthorizedAndPostCreator_receiveNoContentAndNotFound() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(delete("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();

        assertThat(post.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void deletePost_whenUserIsAuthorizedButNotPostCreator_receiveUnauthorizedAndPostExists() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithSecondUserActivated();
        var response = mvc.perform(delete("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(dto.id()).isEqualTo(detail.id());
    }

    @Test
    public void deletePost_whenPostNotExists_receiveNotFound() throws Exception {
        loginWithFirstUserActivated();
        var response = mvc.perform(delete("/posts/post/{uuid}", INVALID_ID)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void deletePost_whenUserIsUnauthorized_receiveForbiddenAndPostExists() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        var response = mvc.perform(delete("/posts/post/{uuid}", dto.id())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(dto.id()).isEqualTo(detail.id());
    }

    @Test
    public void deletePost_whenUserIsAuthorizedButNotActivated_receiveForbiddenAndPostExists() throws Exception {
        loginWithFirstUserActivated();
        var created = mvc.perform(post("/posts").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_POST)).andReturn().getResponse();
        var dto = objectMapper.readValue(created.getContentAsString(), PostCreatedDTO.class);

        loginWithUserNotActivated();
        var response = mvc.perform(delete("/posts/post/{uuid}", dto.id())
                .header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        var post = mvc.perform(get("/posts/post/{uuid}", dto.id())).andReturn().getResponse();
        var detail = objectMapper.readValue(post.getContentAsString(), PostDetailDTO.class);

        assertThat(post.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(dto.id()).isEqualTo(detail.id());
    }

}