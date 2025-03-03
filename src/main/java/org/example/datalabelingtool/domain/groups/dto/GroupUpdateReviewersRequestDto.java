package org.example.datalabelingtool.domain.groups.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupUpdateReviewersRequestDto {
    private List<String> addUserIds;
    private List<String> removeUserIds;
}
