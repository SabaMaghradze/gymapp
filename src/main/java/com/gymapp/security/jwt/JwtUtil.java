package com.gymapp.security.jwt;

import com.gymapp.security.user.UserDetailsCustom;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationTime}")
    private Long jwtExpirationTime;

    public String generateToken(Authentication authentication) {

        Map<String, Object> claims = new HashMap<>();

        UserDetailsCustom userPrincipal = (UserDetailsCustom) authentication.getPrincipal();

        List<String> roles = userPrincipal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationTime))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder() // initializes a JWT parser to process the token.
                .setSigningKey(key()) // This sets the secret key used to sign the JWT. ensures that the token was issued by the correct authority and hasn't been tampered with
                .build() // finalizes the parser construction
                .parseClaimsJws(token) // This decodes and verifies the token. If the token is valid, it extracts the claims (payload) from the JWT.
                .getBody() // This retrieves the claims payload from the token.
                .getSubject(); // Extracts the "sub" (subject) claim, which is typically the username.
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException exc) {
            logger.error("invalid jwt token : {} ", exc.getMessage());
        } catch (ExpiredJwtException exc) {
            logger.error("expired jwt token : {} ", exc.getMessage());
        } catch (UnsupportedJwtException exc) {
            logger.error("unsupported token : {} ", exc.getMessage());
        } catch (IllegalArgumentException exc) {
            logger.error("no claims found : {} ", exc.getMessage());
        }
        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
