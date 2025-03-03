package org.example.datalabelingtool.domain.samples.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.samples.entity.SampleStatus;
import org.example.datalabelingtool.domain.users.dto.UserSimpleResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SampleSameVerResponseDto {
    private String id;
    private String datasetName;
    private String datasetDescription;
    private Long versionId;
    private SampleStatus status;
    private String sampleData;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SampleResponseDto> otherVersionsSamples;
}
