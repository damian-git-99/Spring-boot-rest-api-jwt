package rest.api.example.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import rest.api.example.shared.AbstractConverter;
import rest.api.example.user.dtos.UserDTO;
import rest.api.example.user.dtos.UserDTOConverter;
import rest.api.example.user.entities.User;
import rest.api.example.user.exceptions.UserNotFoundException;
import rest.api.example.user.services.UserService;

import java.util.List;

@Secured("ROLE_USER")
@RestController
@RequestMapping("/api/1.0/users")
public class UserController {

    private final AbstractConverter<User, UserDTO> abstractConverter = new UserDTOConverter();
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<UserDTO> getUsers() {
        return abstractConverter
                .toDTOs(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable(name = "id") Long id) {
        return userService
                .findUserById(id).orElseThrow(() -> new UserNotFoundException("user ot found"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);
    }
}
