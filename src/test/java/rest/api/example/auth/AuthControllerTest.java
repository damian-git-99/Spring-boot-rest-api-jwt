package rest.api.example.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import rest.api.example.auth.jwt.JWTService;
import rest.api.example.user.entities.User;
import rest.api.example.user.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    JWTService jwtService;

    @MockBean
    UserService userService;

    @MockBean
    PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("it returns 200 ok when a valid user is sent")
    void shouldSignIn() throws Exception {
        User user = new User("damian", "damian@gmail.com", "123456");
        mvc.perform(post("/api/1.0/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        then(userService).should().signIn(any(User.class));
    }

    @Test
    @DisplayName("it returns 400 when username, password and email are invalid")
    void shouldNotSignIn() throws Exception {
        User user = new User("", "damian", "");
        Map<String, Object> errors = new HashMap<>();
        errors.put("email", "must be a well-formed email address");
        errors.put("username", "must not be empty");
        errors.put("password", "must not be empty");

        mvc.perform(post("/api/1.0/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(errors)));

        then(userService).should(never()).signIn(any(User.class));

    }


}