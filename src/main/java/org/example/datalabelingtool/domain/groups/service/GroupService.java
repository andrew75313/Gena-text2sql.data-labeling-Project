package org.example.datalabelingtool.domain.groups.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.GroupCreateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupResponseDto;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.domain.groups.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupResponseDto createGroup(GroupCreateRequestDto requestDto) {
        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .name(requestDto.getGroupName())
                .description(requestDto.getGroupDescription())
                .isActive(true)
                .samples(new ArrayList<>())
                .reviewers(new ArrayList<>())
                .build();

        groupRepository.save(group);

        Group savedGroup = groupRepository.findById(group.getId()).orElse(null);

        return toGroupResponseDto(savedGroup);
    }

    private GroupResponseDto toGroupResponseDto(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
