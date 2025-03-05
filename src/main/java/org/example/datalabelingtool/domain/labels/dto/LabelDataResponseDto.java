package org.example.datalabelingtool.domain.labels.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LabelDataResponseDto {
    private final String labelId;
    private final String labelName;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
