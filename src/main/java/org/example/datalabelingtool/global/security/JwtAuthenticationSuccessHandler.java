package org.example.datalabelingtool.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        redisTemplate.opsForValue().set(
                "RefreshToken: " + user.getId(),
                refreshToken,
                Duration.ofMillis(jwtUtil.getRefreshTokenTime())
        );

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESHTOKEN_HEADER, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        String json = new ObjectMapper().writeValueAsString(
                Map.of("statusCode", 200, "msg", "Success to Login.")
        );

        response.getWriter().write(json);
    }
}
