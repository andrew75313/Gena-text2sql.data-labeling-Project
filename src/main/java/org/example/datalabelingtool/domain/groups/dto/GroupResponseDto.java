package org.example.datalabelingtool.domain.groups.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GroupResponseDto {
    private String id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> reviewerIds;
    private List<String> sampleIds;
}
