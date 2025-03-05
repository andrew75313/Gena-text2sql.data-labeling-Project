package org.example.datalabelingtool.domain.labels.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.labels.dto.LabelCreateRequestDto;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.entity.Label;
import org.example.datalabelingtool.domain.labels.repository.LabelRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    public DataResponseDto createLabels(@Valid LabelCreateRequestDto requestDto) {
        List<Label> labelList = new ArrayList<>();

        for(String labelName : requestDto.getLabelNames()) {
            Label foundLabel = labelRepository.findByName(labelName).orElse(null);

            if(foundLabel != null) continue;

            Label label = Label.builder()
                    .id(UUID.randomUUID().toString())
                    .name(formatLabel(labelName))
                    .isActive(Boolean.TRUE)
                    .build();

            labelList.add(label);
        }

        labelRepository.saveAll(labelList);

        return new DataResponseDto(labelList.stream().map(this::toLabelResponseDto).toList());
    }

    private String formatLabel(String labelName) {
        return labelName == null ? null : labelName.trim().replaceAll(" ", "_").toLowerCase();
    }

    private LabelResponseDto toLabelResponseDto(Label label) {
        return LabelResponseDto.builder()
                .labelId(label.getId())
                .labelName(label.getName())
                .build();
    }
}
