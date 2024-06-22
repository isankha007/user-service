package com.sankha.userService.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final int MINUTES = 10;
    @Value("${spring.security.jwt.secret}")
    private String SECRETE ;

    public boolean isTokenValid(String jwt) {
        return true;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails user) {
        return Jwts.builder().setClaims(new HashMap<>())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(getExpiration()).signWith(signingKey()).compact();
    }

    private static Date getExpiration() {
        return new Date(System.currentTimeMillis() + Duration.ofMinutes(MINUTES)
                .toMillis());
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(SECRETE.getBytes());
    }

    public String extractUsername(String jwt) {
        Claims claimsJws = extractAllClaims(jwt);
        return claimsJws.getSubject();

    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(jwt).getBody();
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.getExpiration();
    }
}
