package org.example.datalabelingtool.domain.groups.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class GroupResponseDto {
    private String id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<User> reviewers;
    private Set<Sample> samples;
}
