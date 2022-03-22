package rest.api.example.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import rest.api.example.security.exceptions.ServerErrorException;
import rest.api.example.user.entities.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/1.0/auth", HttpMethod.POST.toString()));
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request
            , HttpServletResponse response) throws AuthenticationException {

        User user;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerErrorException("An error has occur on the server");
        }

        String email = user.getEmail();
        String password = user.getPassword();

        if (email == null || password == null) throw new BadCredentialsException("email and password cannot be empty");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(token);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain
            , Authentication authResult) throws IOException, ServletException {

        String token = "token";
        response.setHeader("Authorization", "Bearer ".concat(token));

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("name", authResult.getName());

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request
            , HttpServletResponse response
            , AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Authentication ERROR: incorrect username or password");

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

    }
}
