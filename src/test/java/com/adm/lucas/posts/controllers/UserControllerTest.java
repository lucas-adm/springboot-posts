package com.adm.lucas.posts.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository repository;

    @AfterEach
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
                "email": "lucasgammmer456123@outlook.com",
                "username": "Lucas2",
                "password": "Senha123",
                "birthDate": "2002-06-22"
            }
            """;

    private static final String INVALID_USER_JSON = """
            {
                "email": "lucasgammmeroutlook.com",
                "username": "Lucas",
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
                "username": "Lucas2",
                "password": "Senha123"
            }
            """;

    private static final String INVALID_USERNAME_LOGIN_JSON = """
            {
                "username": "Luca",
                "password": "Senha123"
            }
            """;

    private static final String INVALID_PASSWORD_LOGIN_JSON = """
            {
                "username": "Lucas",
                "password": "Senha124"
            }
            """;

    private static final String VALID_UPDATE_USER_JSON = """
            {
            	"newEmail": "lucasgammmer777@outlook.com",
            	"newUsername": "Lucas777",
            	"newPassword": "Senha777",
            	"newBirthDate": "2007-07-07"
            }
            """;

    private static final String INVALID_UPDATE_USER_JSON = """
            {
            	"newEmail": "lucasgammmer123456@outlook.com",
            	"newUsername": "Lucas2",
            	"newPassword": "Senha",
            	"newBirthDate": "2002-02-02"
            }
            """;

    private static final String VALID_CHANGE_USER_PHOTO_JSON = """
            {
            	"photo": "https://placehold.co/600x400"
            }
            """;

    private static final String INVALID_CHANGE_USER_PHOTO_JSON = """
            {
            	"foto": "https://placehold.co/600x400"
            }
            """;

    // Register

    @Test
    public void userRegister_whenUserIsValid_receiveCreated() throws Exception {
        var response = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void userRegister_whenUserIsValidButUsernameOrEmailIsUnavailable_receiveNotAcceptable() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));
        var response = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void userRegister_whenUserIsInvalid_receiveBadRequest() throws Exception {
        var response = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(INVALID_USER_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Activate User

    public MockHttpServletResponse activateUser(MockHttpServletResponse method) throws Exception {
        UUID id = objectMapper.readValue(method.getContentAsString(), UserDetailDTO.class).id();
        return mvc.perform(get("/users/activate/{uuid}", id)).andReturn().getResponse();
    }

    @Test
    public void activeUser_whenUserExists_receiveFound() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        assertThat(activateUser(register).getStatus()).isEqualTo(HttpStatus.FOUND.value());
    }

    @Test
    public void activeUser_whenUserNotExists_receiveNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        var response = mvc.perform(get("/users/activate/{uuid}", id)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // GET

    @Test
    public void findUser_whenUserExists_receiveOk() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        var response = mvc.perform(get("/users/{username}", "Lucas")).andReturn().getResponse();
        var dto = objectMapper.readValue(response.getContentAsString(), UserDetailDTO.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(dto.email()).isEqualTo("lucasgammmer123456@outlook.com");
        assertThat(dto.username()).isEqualTo("Lucas");
        assertThat(dto.photo()).isEmpty();
        assertThat(dto.birthDate()).isEqualTo("22 junho, 2002");
    }

    @Test
    public void findUser_whenUserExistsButIsNotActivated_receiveNotFound() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));
        var response = mvc.perform(get("/users/{username}", "Lucas")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void findUser_whenUserNotExists_receiveNotFound() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));
        var response = mvc.perform(get("/users/{username}", "NotFound")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void findAllUsers_receiveOkAndListSize() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var response = mvc.perform(get("/users")).andReturn().getResponse();
        List<UserDetailDTO> users = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(users.size()).isEqualTo(2);
    }

    // Login

    @Test
    public void userLogin_whenLoginIsValid_receiveJSONWebToken() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));

        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();

        UserTokenDTO dto = objectMapper.readValue(response.getContentAsString(), UserTokenDTO.class);
        assertThat(dto.token()).isNotEmpty();
    }

    @Test
    public void userLogin_whenUsernameIsInvalid_receiveNotFound() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));

        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(INVALID_USERNAME_LOGIN_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void userLogin_whenPasswordIsInvalid_receiveBadRequest() throws Exception {
        mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON));

        var response = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(INVALID_PASSWORD_LOGIN_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Edit

    @Test
    public void userEdit_whenUserIsAuthorized_receiveAccepted() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(put("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_UPDATE_USER_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());

        var response = mvc.perform(get("/users/{username}", "Lucas777")).andReturn().getResponse();
        var user = objectMapper.readValue(response.getContentAsString(), UserDetailDTO.class);

        assertThat(user.email()).isEqualTo("lucasgammmer777@outlook.com");
        assertThat(user.username()).isEqualTo("Lucas777");
        assertThat(user.birthDate()).isEqualTo("7 julho, 2007");
    }

    @Test
    public void userEdit_whenUserIsAuthorizedAndEditIsInvalid_receiveBadRequest() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(put("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_UPDATE_USER_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void userEdit_whenUserIsUnauthorized_receiveUnauthorized() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(put("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_UPDATE_USER_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void userEdit_whenUserIsAuthorizedButIsNotActivated_receiveForbidden() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(put("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_UPDATE_USER_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    // Changes User Photo

    @Test
    public void userChangePhoto_whenUserIsAuthorized_receiveAccepted() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(patch("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_CHANGE_USER_PHOTO_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());

        var response = mvc.perform(get("/users/{username}", "Lucas")).andReturn().getResponse();
        var user = objectMapper.readValue(response.getContentAsString(), UserDetailDTO.class);

        assertThat(user.photo()).isEqualTo(Optional.of("https://placehold.co/600x400"));
    }

    @Test
    public void userChangePhoto_whenUserIsAuthorizedAndPhotoIsInvalid_receiveBadRequest() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(patch("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(INVALID_CHANGE_USER_PHOTO_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void userChangePhoto_whenUserIsUnauthorized_receiveUnauthorized() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(patch("/users/edit/{uuid}", uuid).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(VALID_CHANGE_USER_PHOTO_JSON)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    // Deactivate

    @Test
    public void userDeactivate_whenUserIsAuthorized_receiveNoContent() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/deactivate/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        var response = mvc.perform(get("/users/{username}", "Lucas")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void userDeactivate_whenUserIsAuthorizedAndIdNotExists_receiveNotFound() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = UUID.randomUUID();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/deactivate/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();
        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void userDeactivate_whenUserIsUnauthorized_receiveUnauthorized() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/deactivate/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    // Delete

    @Test
    public void userDelete_whenUserIsAuthorized_receiveNoContent() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        var response = mvc.perform(get("/users/{username}", "Lucas")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void userDelete_whenUserIsAuthorizedAndIdNotExists_receiveNotFound() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = UUID.randomUUID();

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(VALID_LOGIN_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void userDelete_whenUserIsUnauthorized_receiveUnauthorized() throws Exception {
        var register = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register);
        UUID uuid = objectMapper.readValue(register.getContentAsString(), User.class).getId();

        var register2 = mvc.perform(post("/users/register").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        activateUser(register2);

        var login = mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(SECOND_VALID_USER_JSON)).andReturn().getResponse();
        var dto = objectMapper.readValue(login.getContentAsString(), UserTokenDTO.class);
        String token = dto.token();

        var JSON = mvc.perform(delete("/users/{uuid}", uuid).header("Authorization", "Bearer " + token)).andReturn().getResponse();

        assertThat(JSON.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}