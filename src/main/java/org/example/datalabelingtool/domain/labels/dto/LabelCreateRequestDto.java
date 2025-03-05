package org.example.datalabelingtool.domain.labels.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class LabelCreateRequestDto {
    private List<String> labelNames;
}
