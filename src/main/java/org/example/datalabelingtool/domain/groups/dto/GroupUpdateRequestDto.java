package org.example.datalabelingtool.domain.groups.dto;

import lombok.Getter;

@Getter
public class GroupUpdateRequestDto {
    private String newGroupName;
    private String newGroupDescription;
}
