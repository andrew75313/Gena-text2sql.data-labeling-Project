package org.example.datalabelingtool.domain.users.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.users.entity.UserRole;

@Getter
@Builder
public class UserSimpleResponseDto {
    private String id;
    private String username;
    private UserRole role;
}
