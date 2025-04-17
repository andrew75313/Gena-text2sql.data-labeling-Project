package org.example.datalabelingtool.global.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.global.security.jwt.JwtProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESHTOKEN_HEADER= "RefreshToken";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(user.getId().toString())
                        .claim(AUTHORIZATION_KEY, user.getRole())
                        .setExpiration(new Date(date.getTime() + jwtProperties.getAccessTokenTime()))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String createRefreshToken(User user) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(user.getId().toString())
                        .claim(AUTHORIZATION_KEY, user.getRole())
                        .setExpiration(new Date(date.getTime() + jwtProperties.getRefreshTokenTime()))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String getRefreshJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESHTOKEN_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public Claims getClaimFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaimFromToken(token);
        return UUID.fromString(claims.getSubject());
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();

        return expiration.getTime();
    }

    public Long getRefreshTokenTime() {
        return jwtProperties.getRefreshTokenTime();
    }
}
