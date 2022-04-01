package rest.api.example.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import rest.api.example.auth.exceptions.InvalidJwtTokenException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {

    private final Date TWO_HOURS = new Date(System.currentTimeMillis() + (10 * 6000 * 120));

    public static final Key SECRET_KEY =
            Keys.hmacShaKeyFor("MI_SUPER_LLAVE_PRIVADA_QWERTY_54321".getBytes(StandardCharsets.UTF_8));


    @Override
    public String createToken(String subject, Map<String, Object> payload) {
        Claims claims = Jwts.claims();
        claims.putAll(payload);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(TWO_HOURS)
                .signWith(SECRET_KEY)
                .compact();
    }

    @Override
    public boolean validateToken(String authorizationHeader) {
        try {
            getClaims(authorizationHeader);
            return true;
        } catch (JwtException e) {
            // Token validation failed
            throw new InvalidJwtTokenException("Expired or invalid JWT token");
        }
    }

    @Override
    public Claims getClaims(String authorizationHeader) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(this.resolveToken(authorizationHeader))
                .getBody();
    }

    // remove the Bearer from the token
    private String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer "))
            return header.replace("Bearer ", "");
        return null;
    }

}
