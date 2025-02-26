package org.example.datalabelingtool.domain.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.users.entity.UserRole;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
