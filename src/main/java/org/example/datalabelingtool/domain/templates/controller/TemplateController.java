package org.example.datalabelingtool.domain.templates.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.templates.dto.TemplateResponseDto;
import org.example.datalabelingtool.domain.templates.service.TemplateService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    @Operation(
            summary = "Get template by ID",
            description = "Retrieves a template by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Template retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TemplateResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Template not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> getTemplateById(
            @Parameter(description = "ID of the template to retrieve") @Valid @PathVariable String id) {
        TemplateResponseDto responseDto = templateService.getTemplateById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all templates",
            description = "Retrieves a list of all templates.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Templates retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllTemplates() {
        DataResponseDto responseDto = templateService.getAllTemplates();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
