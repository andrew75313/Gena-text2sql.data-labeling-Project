package org.example.datalabelingtool.domain.samples.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SampleRejectResponseDto {
    private String sampleId;
    private String status;
    private LocalDateTime rejectedAt;
}
