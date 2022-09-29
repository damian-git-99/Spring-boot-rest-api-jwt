package rest.api.example.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rest.api.example.shared.BaseControllerTest;
import rest.api.example.user.entities.User;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("should return 200 ok")
    @WithMockUser(roles = "USER", username = "damian@gmail.com")
    void shouldGetAllUsers() throws Exception {
        mvc.perform(get("/api/1.0/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 200 ok when user exists")
    @WithMockUser(roles = "USER", username = "damian@gmail.com")
    void shouldGetUserById() throws Exception {
        User user = new User("damian", "damian@gmail.com", "123456");
        given(userService.findUserById(1L)).willReturn(Optional.of(user));

        mvc.perform(get("/api/1.0/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("damian"))
                .andExpect(jsonPath("$.email").value("damian@gmail.com"));

        then(userService).should().findUserById(1L);
    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    @WithMockUser(roles = "USER", username = "damian@gmail.com")
    void shouldNotGetUserById() throws Exception {
        given(userService.findUserById(1L)).willReturn(Optional.empty());

        mvc.perform(get("/api/1.0/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 200 when the user is successfully deleted")
    @WithMockUser(roles = "USER", username = "damian@gmail.com")
    void shouldDeleteUserById() throws Exception {

        mvc.perform(delete("/api/1.0/users/1"))
                .andExpect(status().isOk());

        then(userService).should().deleteUserById(1L);

    }

    @Test
    @DisplayName("should return 200 when the user is successfully updated")
    @WithMockUser(roles = "USER", username = "damian@gmail.com")
    void shouldUpdateUser() throws Exception {
        User userRequest = new User("username updated", "damian222@gmail.com", "2233232");

        given(userService.updateUser(1L, userRequest)).willReturn(userRequest);

        mvc.perform(put("/api/1.0/users/1")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username updated"))
                .andExpect(jsonPath("$.email").value("damian222@gmail.com"));


        then(userService).should().updateUser(1L, userRequest);
    }

}