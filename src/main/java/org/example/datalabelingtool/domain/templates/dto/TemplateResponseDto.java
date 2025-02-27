package org.example.datalabelingtool.domain.templates.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TemplateResponseDto {
    private final String id;
    private final Long templateNo;
    private final String template;
}
