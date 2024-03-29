package rest.api.example.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import rest.api.example.security.SpringSecurityConfig;
import rest.api.example.shared.BaseControllerTest;
import rest.api.example.user.entities.User;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SpringSecurityConfig.class)
class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("it returns 200 ok when a valid user is sent")
    void shouldSignUp() throws Exception {
        User user = new User("damian", "damian@gmail.com", "123456");
        mvc.perform(post("/api/1.0/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        then(userService).should().signUp(any(User.class));
    }

    @Test
    @DisplayName("it returns 400 when username, password and email are invalid")
    void shouldNotSignUp() throws Exception {
        User user = new User("", "damian", "");
        Map<String, Object> errors = new HashMap<>();
        errors.put("email", "must be a well-formed email address");
        errors.put("username", "must not be empty");
        errors.put("password", "must not be empty");

        mvc.perform(post("/api/1.0/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errors)));

        then(userService).should(never()).signUp(any(User.class));

    }


}