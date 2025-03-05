package org.example.datalabelingtool.domain.labels.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.labels.dto.LabelDataResponseDto;
import org.example.datalabelingtool.domain.labels.dto.LabelListRequestDto;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.dto.LabelUpdateRequestDto;
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

    public DataResponseDto createLabels(@Valid LabelListRequestDto requestDto) {
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

    public LabelDataResponseDto getLabelById(String id) {
        Label label = findLabel(id);
        return LabelDataResponseDto.builder()
                .labelId(label.getId())
                .labelName(label.getName())
                .isActive(label.getIsActive())
                .createdAt(label.getCreatedAt())
                .updatedAt(label.getUpdatedAt())
                .build();
    }

    public DataResponseDto getAllLabels() {
        List<Label> labelList = labelRepository.findByIsActiveTrue();
        return new DataResponseDto(labelList.stream().map(this::toLabelResponseDto).toList());
    }

    @Transactional
    public LabelResponseDto updateLabel(String id, LabelUpdateRequestDto requestDto) {
        Label label = findLabel(id);
        label.updateName(formatLabel(requestDto.getNewLabelName()));
        return toLabelResponseDto(label);
    }

    @Transactional
    public void deleteLabels(LabelListRequestDto requestDto) {
        List<Label> labelList = new ArrayList<>();

        for(String labelName : requestDto.getLabelNames()) {
            Label label = labelRepository.findByName(labelName).orElseThrow(
                    ()-> new EntityNotFoundException("Label not found")
            );

            label.updateStatus(Boolean.FALSE);
        }
    }

    private Label findLabel(String id) {
        return labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Label not found")
        );
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
