package org.example.datalabelingtool.domain.users.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.dto.UserCreateRequestDto;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserUpdateRequestDto;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.entity.UserRole;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.code}")
    private String adminCode;

    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        String dtoAdminCode = requestDto.getAdminCode();
        UserRole role = UserRole.USER;

        if (dtoAdminCode != null && dtoAdminCode.equals(adminCode)) {
            role = UserRole.ADMIN;
        }

        String uuid = UUID.randomUUID().toString();

        User user = User.builder()
                .id(uuid)
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        return toResponseDto(user);
    }

    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        return toResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto requestDto) {
        String username = requestDto.getCurrentUsername();
        String password = requestDto.getCurrentPassword();
        String newUsername = requestDto.getNewUsername();
        String newPassword = requestDto.getNewPassword();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        if(StringUtils.hasText(newUsername)) user.updateUsername(newUsername);
        if(StringUtils.hasText(newPassword)) user.updatePassword(passwordEncoder.encode(newPassword));

        return toResponseDto(user);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
