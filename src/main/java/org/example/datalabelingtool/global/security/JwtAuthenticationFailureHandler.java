package org.example.datalabelingtool.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String msg;

        if (exception instanceof UsernameNotFoundException) {
            msg = "Can't find user.";
        } else if (exception instanceof BadCredentialsException) {
            msg = "Check your username and password again.";
        } else {
            msg = "Fail to Login.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String json = new ObjectMapper().writeValueAsString(
                Map.of("statusCode", 401, "msg", msg)
        );

        response.getWriter().write(json);
    }
}
