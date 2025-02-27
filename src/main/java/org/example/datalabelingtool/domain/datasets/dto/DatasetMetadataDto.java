package org.example.datalabelingtool.domain.datasets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DatasetMetadataDto {
    @NotBlank
    private String datasetName;
    private String datasetDescription;
}