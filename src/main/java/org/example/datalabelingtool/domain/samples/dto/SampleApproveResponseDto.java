package org.example.datalabelingtool.domain.samples.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SampleApproveResponseDto {
    private String sampleId;
    private String status;
    private LocalDateTime approvedAt;
}
