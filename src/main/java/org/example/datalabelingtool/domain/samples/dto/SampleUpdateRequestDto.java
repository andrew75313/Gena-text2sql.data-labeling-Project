package org.example.datalabelingtool.domain.samples.dto;

import lombok.Getter;

@Getter
public class SampleUpdateRequestDto {
    private String username;
    private String sqlQuery;
    private String naturalQuestion;
    private Boolean passed;
    private Boolean deleted;
}
