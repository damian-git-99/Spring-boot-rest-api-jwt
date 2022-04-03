package rest.api.example.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserExceptionsHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(map);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<Map<String, Object>> handleEmailInUse(EmailAlreadyExistsException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return ResponseEntity
                .badRequest()
                .body(map);
    }

}
