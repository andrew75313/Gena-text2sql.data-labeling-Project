package org.example.datalabelingtool.domain.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequestDto {
    private String newUsername;
    private String newPassword;
}
