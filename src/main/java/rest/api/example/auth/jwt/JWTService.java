package rest.api.example.auth.jwt;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JWTService {

     String createToken(String subject, Map<String, Object> payload);
     boolean validateToken(String header);
     Claims getClaims(String authorizationHeader);

}
