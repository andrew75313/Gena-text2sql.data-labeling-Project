package org.example.datalabelingtool.domain.labels.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabelResponseDto {
    private final String labelId;
    private final String labelName;
}
