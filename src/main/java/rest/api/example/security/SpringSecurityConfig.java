package rest.api.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import rest.api.example.auth.jwt.JWTService;
import rest.api.example.security.filters.AuthenticationFilter;
import rest.api.example.security.filters.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final HttpConfigurer httpConfigurer;

    @Autowired
    public SpringSecurityConfig(UserDetailsService userDetailsService
            , JWTService jwtService
            , PasswordEncoder passwordEncoder
            , HttpConfigurer httpConfigurer) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.httpConfigurer = httpConfigurer;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/1.0/auth").permitAll()
                .antMatchers(HttpMethod.POST, "/api/1.0/users").permitAll()
                .antMatchers("/api/**").hasRole("USER")
                .anyRequest().permitAll();
        http.apply(httpConfigurer);
        http.addFilterBefore(new JWTAuthorizationFilter(jwtService), AuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

}
