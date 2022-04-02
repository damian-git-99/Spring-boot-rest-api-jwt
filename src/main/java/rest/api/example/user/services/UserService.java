package rest.api.example.user.services;

import rest.api.example.user.entities.User;

import java.util.List;

public interface UserService {
    User findUserByEmail(String email);
    void signIn(User user);
    List<User> findAllUsers();
}
