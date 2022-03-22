package rest.api.example.user.services;

import rest.api.example.user.entities.User;

public interface UserService {
    User findUserByEmail(String email);
}
