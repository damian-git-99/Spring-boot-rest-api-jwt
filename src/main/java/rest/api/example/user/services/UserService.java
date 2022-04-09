package rest.api.example.user.services;

import rest.api.example.user.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findUserByEmail(String email);
    void signIn(User user);
    List<User> findAllUsers();
    Optional<User> findUserById(Long userId);
    void deleteUserById(Long id);
    User updateUser(Long id, User newUser);
}
