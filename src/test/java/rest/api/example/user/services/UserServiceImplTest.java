package rest.api.example.user.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import rest.api.example.user.daos.UserDao;
import rest.api.example.user.exceptions.EmailAlreadyExistsException;
import rest.api.example.user.role.Role;
import rest.api.example.user.entities.User;
import rest.api.example.user.exceptions.UserNotFoundException;
import rest.api.example.user.role.RoleDao;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private RoleDao roleDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("it should load the user when user exits with that email")
    void shouldLoadUserByUsername() {
        User userData = new User("damian", "damian@gmail.com", "123456");
        var roles = List.of(new Role("USER"));
        userData.setRoles(roles);
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.of(userData));

        UserDetails user = userService.loadUserByUsername("damian@gmail.com");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("damian@gmail.com");
        assertThat(user.getPassword()).isEqualTo("123456");
        assertThat(user.getAuthorities()).hasSize(1);
        then(userDao).should().findUserByEmail("damian@gmail.com");
    }

    @Test
    @DisplayName("it should not load the user when user does not exists with that email")
    void shouldNotLoadUserByUsername() {
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.ofNullable(null));

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            UserDetails user = userService.loadUserByUsername("damian@gmail.com");
        });

        assertThat(exception.getClass()).isEqualTo(UsernameNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("User not found");
        then(userDao).should().findUserByEmail("damian@gmail.com");
    }

    @Test
    @DisplayName("it should find the user by email when user with that email exist")
    void shouldFindUserByEmail() {
        User userData = new User("damian", "damian@gmail.com", "123456");
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.of(userData));

        User user = userService.findUserByEmail("damian@gmail.com");
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("damian@gmail.com");
        assertThat(user.getUsername()).isEqualTo("damian");
        assertThat(user.getPassword()).isEqualTo("123456");
        then(userDao).should().findUserByEmail("damian@gmail.com");

    }

    @Test
    @DisplayName("it should not find the user by email when user with that email does not exist")
    void shouldNotFindUserByEmail() {
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.ofNullable(null));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            User user = userService.findUserByEmail("damian@gmail.com");
        });

        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("User not found");
        then(userDao).should().findUserByEmail("damian@gmail.com");
    }

    @Test
    @DisplayName("should register a user correctly")
    void shouldSignIn() {
        Role role = new Role("USER");
        User user = new User("damian", "damian@gmail.com", "123456");
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.ofNullable(null));
        given(passwordEncoder.encode("123456")).willReturn("hashedPassword");
        given(roleDao.findRoleByRole("USER")).willReturn(Optional.of(role));
        given(userDao.save(user)).willAnswer(invocation -> {
            User user1 = invocation.getArgument(0);
            user1.setId(1L);
            return user;
        });

        userService.signIn(user);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getEmail()).isEqualTo("damian@gmail.com");

        then(userDao).should().findUserByEmail("damian@gmail.com");
        then(passwordEncoder).should().encode("123456");
        then(userDao).should().save(user);

    }

    @Test
    @DisplayName("should throw EmailAlreadyExistsException when email already exists")
    void shouldNotSingIn() {
        User user = new User("damian", "damian@gmail.com", "123456");
        User userInDB = new User("Irving", "damian@gmail.com", "123");
        given(userDao.findUserByEmail("damian@gmail.com")).willReturn(Optional.ofNullable(userInDB));

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.signIn(user);
        });

        assertThat(exception.getClass()).isEqualTo(EmailAlreadyExistsException.class);
        assertThat(exception.getMessage()).isEqualTo("the email already exists");
        then(userDao).should(never()).save(user);
    }


}