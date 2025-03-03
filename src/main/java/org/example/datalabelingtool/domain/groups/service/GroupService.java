package org.example.datalabelingtool.domain.groups.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.GroupCreateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupResponseDto;
import org.example.datalabelingtool.domain.groups.dto.GroupUpdateRequestDto;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.domain.groups.repository.GroupRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .samples(new HashSet<>())
                .reviewers(new HashSet<>())
                .build();

        groupRepository.save(group);

        Group savedGroup = groupRepository.findById(group.getId()).orElse(null);

        return toGroupResponseDto(savedGroup);
    }

    public GroupResponseDto getGroupById(String id) {
        Group group = findGroup(id);

        return toGroupResponseDto(group);
    }

    public DataResponseDto getAllGroups() {
        List<GroupResponseDto> responseDtoList = groupRepository.findAllByIsActiveTrue().stream()
                .map(this::toGroupResponseDto)
                .collect(Collectors.toList());
        return new DataResponseDto(responseDtoList);
    }

    @Transactional
    public GroupResponseDto updateGroup(@Valid String id, GroupUpdateRequestDto requestDto) {
        String newGroupName = requestDto.getNewGroupName();
        String newGroupDescription = requestDto.getNewGroupDescription();

        Group group = findGroup(id);

        if (!group.getIsActive()) throw new EntityNotFoundException("Group not found");

        if (newGroupName != null && !newGroupName.isEmpty()) group.updateName(newGroupName);
        if (newGroupDescription != null && !newGroupDescription.isEmpty()) group.updateDescription(newGroupDescription);

        return toGroupResponseDto(group);
    }

    @Transactional
    public void deleteGroup(@Valid String id) {
        Group group = groupRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("Group not found")
        );

        group.updateIsActive(false);
    }

    private Group findGroup(String id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Group not found")
        );
    }

    private GroupResponseDto toGroupResponseDto(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .reviewers(group.getReviewers())
                .samples(group.getSamples())
                .build();
    }
}
