package org.example.datalabelingtool.domain.samples.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SampleUpdateRequestDto {
    private String username;
    private String sqlQuery;
    private String naturalQuestion;
    private List<String> labels;
    private Boolean passed;
    private Boolean deleted;
}
