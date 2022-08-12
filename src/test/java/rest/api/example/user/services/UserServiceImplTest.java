package rest.api.example.user.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
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

        userService.signUp(user);

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
            userService.signUp(user);
        });

        assertThat(exception.getClass()).isEqualTo(EmailAlreadyExistsException.class);
        assertThat(exception.getMessage()).isEqualTo("the email already exists");
        then(userDao).should(never()).save(user);
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        User user = new User("damian", "damian@gmail.com", "123456");
        User user2 = new User("jose", "jose@gmail.com", "123456");
        User user3 = new User("carlos", "carlos@gmail.com", "123456");

        given(userDao.findAll()).willReturn(List.of(user, user2, user3));

        List<User> users = userService.findAllUsers();

        assertThat(users.size()).isEqualTo(3);
        assertThat(users).contains(user, user2, user3);
    }

    @Test
    @DisplayName("should return user by id")
    void shouldReturnUserById() {
        User userData = new User("damian", "damian@gmail.com", "123456");
        userData.setId(1L);

        given(userService.findUserById(1L)).willReturn(Optional.of(userData));

        Optional<User> user = userService.findUserById(1L);

        assertThat(user).isPresent();
    }

    @Test
    @DisplayName("should delete user By Id")
    void shouldDeleteUserById() {
        userService.deleteUserById(1L);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        then(userDao).should().deleteById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should update user")
    void shouldUpdateUser() {
        User userInDb = new User("damian","damian@gmail.com","12345");
        userInDb.setId(1L);
        User updatedUser = new User("damian_update","damian22@gmail.com","12345");

        given(userService.findUserById(1L)).willReturn(Optional.of(userInDb));
        given(userDao.save(userInDb)).willReturn(userInDb);
        given(userDao.findUserByEmail("damian22@gmail.com")).willReturn(Optional.empty());

        User user = userService.updateUser(1L, updatedUser);

        assertThat(user.getUsername()).isEqualTo("damian_update");
        assertThat(user.getEmail()).isEqualTo("damian22@gmail.com");

        then(userDao).should().save(any(User.class));
    }

    @Test
    @DisplayName("Should not update user when email already exists")
    void shouldNotUpdate() {
        User userInDb = new User("damian","damian@gmail.com","12345");
        userInDb.setId(1L);
        User updatedUser = new User("damian_update","damian22@gmail.com","12345");

        given(userService.findUserById(1L)).willReturn(Optional.of(userInDb));
        given(userDao.findUserByEmail("damian22@gmail.com")).willReturn(Optional.of(new User()));

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(1L, updatedUser);
        });

        assertThat(exception.getClass()).isEqualTo(EmailAlreadyExistsException.class);
        assertThat(exception.getMessage()).isEqualTo("the email already exists");
        then(userDao).should(never()).save(any(User.class));
    }
}