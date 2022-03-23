package rest.api.example.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rest.api.example.auth.exceptions.InvalidJwtTokenException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class JWTServiceImplTest {

    private JWTService jwtService = new JWTServiceImpl();


    @Test
    @DisplayName("it should create a token")
    void shouldCreateAccessToken() {
        String token = jwtService.createToken("damian@gmail.com", new HashMap<>());
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("it should validate a token correctly")
    void shouldValidateToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", 2);

        String token = "Bearer " + jwtService.createToken("damian@gmail.com", payload);
        boolean result = jwtService.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("it should fail trying to validate a token")
    void shouldFailValidateToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", 2);

        String finalToken = "Bearer " + jwtService.createToken("damian@gmail.com", payload) + "1"; // concatenate a character to make it an invalid token

        Exception exception = assertThrows(InvalidJwtTokenException.class, () -> {
            boolean result = jwtService.validateToken(finalToken);
        });

        assertThat(exception.getClass()).isEqualTo(InvalidJwtTokenException.class);
        assertThat(exception.getMessage()).isEqualTo("Expired or invalid JWT token");
    }

}