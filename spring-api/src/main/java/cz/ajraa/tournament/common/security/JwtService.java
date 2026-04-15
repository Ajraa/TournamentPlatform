package cz.ajraa.tournament.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String GenerateToken(Long userId, List<String> roleNames) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("roles", roleNames)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Platnost 24 hodin
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserId(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String jwt) {
        Claims claims = extractAllClaims(jwt);

        return (List<String>) claims.get("roles");
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
