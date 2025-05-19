package org.example.datalabelingtool.global.security.config;

import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.global.security.authorization.JwtAuthenticationEntryPoint;
import org.example.datalabelingtool.global.security.handler.JwtAccessDeniedHandler;
import org.example.datalabelingtool.global.security.handler.JwtAuthenticationFailureHandler;
import org.example.datalabelingtool.global.security.authentication.JwtAuthenticationFilter;
import org.example.datalabelingtool.global.security.handler.JwtAuthenticationSuccessHandler;
import org.example.datalabelingtool.global.security.authorization.JwtAuthorizationFilter;
import org.example.datalabelingtool.global.security.util.JwtUtil;
import org.example.datalabelingtool.global.security.user.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        filter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, authenticationEntryPoint, accessDeniedHandler);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/datasets/upload").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/datasets/download/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/datasets/latest-versions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/datasets/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/datasets/*/reject").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/groups").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/groups").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/groups/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/groups/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/groups/*/update-reviewers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/groups/*/update-samples").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/labels").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/labels/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/labels").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}
