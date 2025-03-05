package org.example.datalabelingtool.domain.labels.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LabelUpdateRequestDto {
    @NotNull
    private String newLabelName;
}
