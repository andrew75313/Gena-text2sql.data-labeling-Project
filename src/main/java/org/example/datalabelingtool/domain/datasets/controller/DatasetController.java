package org.example.datalabelingtool.domain.datasets.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.datasets.service.DatasetService;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
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
                                                            @RequestPart("metadata") DatasetMetadataDto metadata) throws Exception{
        datasetService.uploadCsvFile(file, metadata);
        return new ResponseEntity<>(new MessageResponseDto("Dataset uploaded successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleResponseDto> getSampleById(@Valid @PathVariable String id) {
        SampleResponseDto responseDto = datasetService.getSampleById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
