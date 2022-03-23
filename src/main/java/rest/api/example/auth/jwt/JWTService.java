package rest.api.example.auth.jwt;

import java.util.Map;

public interface JWTService {

    public String createToken(String subject, Map<String, Object> payload);
    public boolean validateToken(String header);

}
