package rest.api.example.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.api.example.shared.AbstractConverter;
import rest.api.example.user.dtos.UserDTO;
import rest.api.example.user.dtos.UserDTOConverter;
import rest.api.example.user.entities.User;
import rest.api.example.user.services.UserService;

import java.util.List;

@Secured("ROLE_USER")
@RestController
@RequestMapping("/api/1.0/users")
public class UserController {

    private AbstractConverter<User, UserDTO> abstractConverter = new UserDTOConverter();
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<UserDTO> getUsers() {
        return abstractConverter
                .toDTOs(userService.findAllUsers());
    }

}
