package org.example.datalabelingtool.domain.templates.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.templates.dto.TemplateResponseDto;
import org.example.datalabelingtool.domain.templates.entity.Template;
import org.example.datalabelingtool.domain.templates.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    public List<TemplateResponseDto> getAllTemplates() {
        return templateRepository.findAllOrderByTemplateNoAsc().stream()
                .map(this::toTemplateResponseDto)
                .collect(Collectors.toList());
    }


    private TemplateResponseDto toTemplateResponseDto(Template template) {
        return TemplateResponseDto.builder()
                .id(template.getId())
                .templateNo(template.getTemplateNo())
                .template(template.getContent())
                .build();
    }
}