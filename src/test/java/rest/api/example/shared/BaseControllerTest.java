package rest.api.example.shared;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import rest.api.example.auth.jwt.JWTService;
import rest.api.example.user.services.UserService;

public class BaseControllerTest {

    @MockBean
    protected JWTService jwtService;
    @MockBean
    protected UserService userService;
    @MockBean
    protected PasswordEncoder passwordEncoder;
}
