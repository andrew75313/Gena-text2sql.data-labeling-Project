package org.example.datalabelingtool.domain.users.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleWithUpdateStatusDto;
import org.example.datalabelingtool.domain.users.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserSampleResponseDto {
    private String id;
    private String username;
    private UserRole role;
    private List<String> groupIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SampleWithUpdateStatusDto> samples;
}
