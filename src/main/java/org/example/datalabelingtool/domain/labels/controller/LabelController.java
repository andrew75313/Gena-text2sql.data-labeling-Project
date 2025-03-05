package org.example.datalabelingtool.domain.labels.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.labels.dto.LabelDataResponseDto;
import org.example.datalabelingtool.domain.labels.dto.LabelListRequestDto;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.dto.LabelUpdateRequestDto;
import org.example.datalabelingtool.domain.labels.service.LabelService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    @Operation(
            summary = "Create labels",
            description = "Create new labels based on the provided label list.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Labels created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("")
    public ResponseEntity<DataResponseDto> createLabels(@Valid @RequestBody LabelListRequestDto requestDto) {
        DataResponseDto dataResponseDto = labelService.createLabels(requestDto);
        return new ResponseEntity<>(dataResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get label by ID",
            description = "Retrieve a label by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Label found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LabelDataResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Label not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LabelDataResponseDto> getLabelById(@Parameter(description = "Label ID") @PathVariable String id) {
        LabelDataResponseDto responseDto = labelService.getLabelById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all labels",
            description = "Retrieve all available labels.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Labels retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllLabels() {
        DataResponseDto dataResponseDto = labelService.getAllLabels();
        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update label",
            description = "Update an existing label using its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Label updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LabelResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Label not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseDto> updateLabel(
            @Parameter(description = "Label ID") @PathVariable String id,
            @Valid @RequestBody LabelUpdateRequestDto requestDto) {
        LabelResponseDto responseDto = labelService.updateLabel(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete labels",
            description = "Delete labels based on the provided list of labels.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Labels deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @DeleteMapping("")
    public ResponseEntity<?> deleteLabels(@Valid @RequestBody LabelListRequestDto requestDto) {
        labelService.deleteLabels(requestDto);
        return ResponseEntity.noContent().build();
    }
}
