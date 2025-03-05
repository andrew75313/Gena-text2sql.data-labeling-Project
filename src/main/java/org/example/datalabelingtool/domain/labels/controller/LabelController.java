package org.example.datalabelingtool.domain.labels.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.labels.dto.LabelCreateRequestDto;
import org.example.datalabelingtool.domain.labels.service.LabelService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lables")
public class LabelController {

    private final LabelService labelService;

    @PostMapping("")
    public ResponseEntity<DataResponseDto> createLabels(@Valid @RequestBody LabelCreateRequestDto requestDto) {
        DataResponseDto dataResponseDto = labelService.createLabels(requestDto);
        return new ResponseEntity<>(dataResponseDto, HttpStatus.CREATED);
    }

    // retrieve all labels

    // update label

    // delete labels
}
