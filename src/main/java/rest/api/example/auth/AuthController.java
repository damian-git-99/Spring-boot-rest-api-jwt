package rest.api.example.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.api.example.user.entities.User;
import rest.api.example.user.services.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/users")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<?> signIn(@RequestBody @Valid User user
            , BindingResult result) {

        if (result.hasErrors()) return errors(result);
        userService.signIn(user);
        return ResponseEntity.ok("User Created");
    }

    public static ResponseEntity<?> errors(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

}
