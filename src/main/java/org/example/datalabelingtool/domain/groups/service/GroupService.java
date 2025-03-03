package org.example.datalabelingtool.domain.groups.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.GroupCreateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupResponseDto;
import org.example.datalabelingtool.domain.groups.dto.GroupUpdateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupUpdateReviewersRequestDto;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.domain.groups.repository.GroupRepository;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
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
    private final UserRepository userRepository;

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

    @Transactional
    public GroupResponseDto updateReviewers(String id, GroupUpdateReviewersRequestDto requestDto) {
        Group group = findGroup(id);

        for (String userId : requestDto.getAddUserIds()) {
            User user = findUser(userId);
            group.addReviewer(user);
        }

        for (String userId : requestDto.getRemoveUserIds()) {
            User user = findUser(userId);
            group.removeReviewer(user);
        }

        return toGroupResponseDto(group);
    }

    private Group findGroup(String id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Group not found")
        );
    }

    private User findUser(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
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
                .reviewerIds(group.getReviewers().stream()
                        .map(User::getId)
                        .toList())
                .sampleIds(group.getSamples().stream()
                        .map(Sample::getId)
                        .toList())
                .build();
    }
}
