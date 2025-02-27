package org.example.datalabelingtool.domain.templates.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.templates.dto.TemplateResponseDto;
import org.example.datalabelingtool.domain.templates.entity.Template;
import org.example.datalabelingtool.domain.templates.repository.TemplateRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateResponseDto getTemplateById(@Valid String id) {
        Template template = templateRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Template not found")
        );

        return toTemplateResponseDto(template);
    }

    private TemplateResponseDto toTemplateResponseDto(Template template) {
        return TemplateResponseDto.builder()
                .id(template.getId())
                .templateNo(template.getTemplateNo())
                .template(template.getContent())
                .build();
    }
}
