package rest.api.example.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rest.api.example.shared.BaseControllerTest;
import rest.api.example.user.entities.User;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principalUser;

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        principalUser = user("damian@gmail.com").password("pass").roles("USER");
        mapper = new ObjectMapper();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("should return 200 ok")
    void shouldGetAllUsers() throws Exception {
        mvc.perform(get("/api/1.0/users").with(principalUser))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("should return 200 ok when user exists")
    void shouldGetUserById() throws Exception {
        User user = new User("damian", "damian@gmail.com", "123456");
        given(userService.findUserById(1L)).willReturn(Optional.of(user));

        mvc.perform(get("/api/1.0/users/1").with(principalUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("damian"))
                .andExpect(jsonPath("$.email").value("damian@gmail.com"));

        then(userService).should().findUserById(1L);
    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    void shouldNotGetUserById() throws Exception {
        User user = null;
        given(userService.findUserById(1L)).willReturn(Optional.ofNullable(user));

        mvc.perform(get("/api/1.0/users/1").with(principalUser))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("should return 200 when the user is successfully deleted")
    void shouldDeleteUserById() throws Exception {

        mvc.perform(delete("/api/1.0/users/1").with(principalUser))
                .andExpect(status().isOk());

        then(userService).should().deleteUserById(1L);

    }

}