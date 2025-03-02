package org.example.datalabelingtool.domain.groups.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GroupDataResponseDto {
    private String id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<User> reviewers;
    private List<Sample> samples;
}
