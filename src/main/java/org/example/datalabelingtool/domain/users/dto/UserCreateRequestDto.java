package org.example.datalabelingtool.domain.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String adminCode;
}
