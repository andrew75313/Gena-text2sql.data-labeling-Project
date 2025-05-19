package org.example.datalabelingtool.global.security.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.global.security.auth.service.AuthService;
import org.example.datalabelingtool.global.security.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {

        String refreshToken = jwtUtil.getRefreshJwtFromHeader(request);

        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            String newRefreshToken = authService.refreshRefreshToken(refreshToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(JwtUtil.AUTHORIZATION_HEADER, newAccessToken)
                    .header(JwtUtil.REFRESHTOKEN_HEADER, newRefreshToken)
                    .body("Access token has been refreshed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token is invalid or expired.");
        }
    }

}
