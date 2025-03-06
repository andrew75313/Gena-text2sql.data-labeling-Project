package org.example.datalabelingtool.domain.samples.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.samples.entity.SampleStatus;
import org.example.datalabelingtool.domain.users.dto.UserSimpleResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SampleResponseDto {
    private String id;
    private Long sampleId;
    private String naturalQuestion;
    private String sqlQuery;
    private String datasetName;
    private String datasetDescription;
    private Long versionId;
    private SampleStatus status;
    private List<LabelResponseDto> labels;
    private UserSimpleResponseDto updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
