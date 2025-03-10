package org.example.datalabelingtool.domain.datasets.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.datasets.service.DatasetService;
import org.example.datalabelingtool.domain.samples.dto.*;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.example.datalabelingtool.global.dto.MessageResponseDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    @Operation(
            summary = "Upload CSV file",
            description = "Uploads a CSV file along with its metadata.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dataset uploaded successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid file or metadata")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDto> uploadCsvFile(
            @Parameter(description = "CSV file to be uploaded") @RequestPart("file") MultipartFile file,
            @Parameter(description = "Metadata associated with the dataset") @RequestPart("metadata") DatasetMetadataDto metadata) throws Exception {
        datasetService.uploadCsvFile(file, metadata);
        return new ResponseEntity<>(new MessageResponseDto("Dataset uploaded successfully"), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Download CSV file",
            description = "Downloads a CSV file associated with a specific dataset.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "CSV file downloaded successfully",
                            content = @Content(mediaType = "text/csv")),
                    @ApiResponse(responseCode = "404", description = "Dataset not found")
            }
    )
    @GetMapping("/download/{dataset_name}")
    public ResponseEntity<InputStreamResource> downloadCsvFile(@PathVariable(name = "dataset_name") String datasetName) {
        InputStreamResource resource = datasetService.getCsvFile(datasetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datasetName.replace(" ", "_") + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    @Operation(
            summary = "Get latest updated samples",
            description = "Retrieves the latest updated samples from the dataset.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Latest updated samples retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping("/latest-versions")
    public ResponseEntity<DataResponseDto> getLatestUpdatedSamples() {
        DataResponseDto responseDto = datasetService.getLatestUpdatesSamples();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get requested samples",
            description = "Fetches all requested samples.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Requested samples retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping("/requested")
    public ResponseEntity<DataResponseDto> getRequestedSamples() {
        DataResponseDto responseDto = datasetService.getRequestedSamples();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get sample by ID",
            description = "Fetches a sample by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sample retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SampleSameVerResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Sample not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SampleSameVerResponseDto> getSampleById(@Valid @PathVariable String id) {
        SampleSameVerResponseDto responseDto = datasetService.getSampleById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update sample",
            description = "Updates a sample by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sample updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SampleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Sample not found")
            }
    )
    @PostMapping("/{id}")
    public ResponseEntity<DataResponseDto> updateSample(@Valid @PathVariable String id,
                                                                 @Valid @RequestBody SampleUpdateRequestDto requestDto) throws JsonProcessingException {
        DataResponseDto responseDto = datasetService.updateSample(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Approve sample",
            description = "Approves a sample by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sample approved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SampleApproveResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Sample not found")
            }
    )
    @PatchMapping("/{id}/approve")
    public ResponseEntity<SampleApproveResponseDto> approveSample(@Valid @PathVariable String id) throws JsonProcessingException {
        SampleApproveResponseDto responseDto = datasetService.approveSample(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Reject sample",
            description = "Rejects a sample by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sample rejected successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SampleRejectResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Sample not found")
            }
    )
    @PatchMapping("/{id}/reject")
    public ResponseEntity<SampleRejectResponseDto> rejectSample(@Valid @PathVariable String id) {
        SampleRejectResponseDto responseDto = datasetService.rejectSample(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
