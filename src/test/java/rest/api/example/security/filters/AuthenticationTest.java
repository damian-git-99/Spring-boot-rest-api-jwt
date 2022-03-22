package rest.api.example.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import rest.api.example.user.daos.UserDao;
import rest.api.example.user.entities.Role;
import rest.api.example.user.entities.User;


import java.util.List;
import java.util.Optional;


import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("it returns 200 when email and password are correct")
    void successfulAuthentication() throws Exception {
        var roles = List.of(new Role("USER"));
        User user = new User("damian", "damian@gmail.com", passwordEncoder.encode("1234"));
        user.setRoles(roles);
        User user1 = new User("", "damian@gmail.com", "1234");

        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.of(user));

        mvc.perform(post("/api/1.0/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }

    @Test
    @DisplayName("it should failed when password is not correct")
    void unsuccessfulAuthentication() throws Exception {
        var roles = List.of(new Role("USER"));
        User user = new User("damian", "damian@gmail.com", passwordEncoder.encode("1234"));
        user.setRoles(roles);
        User user1 = new User("", "damian@gmail.com", "123456");

        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.of(user));

        mvc.perform(post("/api/1.0/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication ERROR: incorrect username or password"));

    }

}