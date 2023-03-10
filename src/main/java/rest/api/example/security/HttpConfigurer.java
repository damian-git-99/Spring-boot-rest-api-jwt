package rest.api.example.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;
import rest.api.example.auth.jwt.JWTService;
import rest.api.example.security.filters.AuthenticationFilter;

@Component
public class HttpConfigurer extends AbstractHttpConfigurer<HttpConfigurer, HttpSecurity> {

    private final JWTService jwtService;

    public HttpConfigurer(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        AuthenticationFilter filter = new AuthenticationFilter(authenticationManager, jwtService);
        http.addFilter(filter);
    }

}
