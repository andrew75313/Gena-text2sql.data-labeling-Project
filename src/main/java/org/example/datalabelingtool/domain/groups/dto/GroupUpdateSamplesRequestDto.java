package org.example.datalabelingtool.domain.groups.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupUpdateSamplesRequestDto {
    private List<String> addSampleIds;
    private List<String> removeSampleIds;
}
