package org.example.datalabelingtool.domain.datasets.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.datasets.service.DatasetService;
import org.example.datalabelingtool.domain.samples.dto.SampleApproveResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleRejectResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleUpdateRequestDto;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.example.datalabelingtool.global.dto.MessageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponseDto> uploadCsvFile(@RequestPart("file") MultipartFile file,
                                                            @RequestPart("metadata") DatasetMetadataDto metadata) throws Exception {
        datasetService.uploadCsvFile(file, metadata);
        return new ResponseEntity<>(new MessageResponseDto("Dataset uploaded successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/latest-versions")
    public ResponseEntity<DataResponseDto> getLatestUpdatedSamples() {
        DataResponseDto responseDto = datasetService.getLatestUpdatesSamples();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/requested")
    public ResponseEntity<DataResponseDto> getRequestedSamples() {
        DataResponseDto responseDto = datasetService.getRequestedSamples();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleResponseDto> getSampleById(@Valid @PathVariable String id) {
        SampleResponseDto responseDto = datasetService.getSampleById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<SampleResponseDto> updateSample(@Valid @PathVariable String id,
                                                          @Valid @RequestBody SampleUpdateRequestDto requestDto) throws JsonProcessingException {
        SampleResponseDto responseDto = datasetService.updateSample(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<SampleApproveResponseDto> approveSample(@Valid @PathVariable String id) {
        SampleApproveResponseDto responseDto = datasetService.approveSample(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<SampleRejectResponseDto> rejectSample(@Valid @PathVariable String id) {
        SampleRejectResponseDto responseDto = datasetService.rejectSample(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
