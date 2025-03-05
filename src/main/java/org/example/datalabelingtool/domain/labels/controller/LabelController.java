package org.example.datalabelingtool.domain.labels.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.labels.dto.LabelCreateRequestDto;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.dto.LabelUpdateRequestDto;
import org.example.datalabelingtool.domain.labels.service.LabelService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseDto> getLabelById(@Valid @PathVariable String id) {
        LabelResponseDto responseDto = labelService.getLabelById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllLabels() {
        DataResponseDto dataResponseDto = labelService.getAllLabels();
        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseDto> updateLabel(@Valid @PathVariable String id,
                                                       @Valid @RequestBody LabelUpdateRequestDto requestDto) {
        LabelResponseDto responseDto = labelService.updateLabel(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // delete labels
}
