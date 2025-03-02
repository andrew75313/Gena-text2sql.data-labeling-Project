package org.example.datalabelingtool.domain.groups.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GroupCreateRequestDto {
    @NotNull
    private String groupName;
    private String groupDescription;
}
