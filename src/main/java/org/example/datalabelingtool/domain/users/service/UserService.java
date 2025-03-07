package org.example.datalabelingtool.domain.users.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.domain.groups.repository.GroupRepository;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.entity.Label;
import org.example.datalabelingtool.domain.labels.repository.LabelRepository;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleWithUpdateStatusDto;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.samples.repository.SampleRepository;
import org.example.datalabelingtool.domain.users.dto.*;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.entity.UserRole;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final SampleRepository sampleRepository;
    private final LabelRepository labelRepository;

    @Value("${app.admin.code}")
    private String adminCode;

    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        String dtoAdminCode = requestDto.getAdminCode();
        UserRole role = UserRole.USER;

        if (dtoAdminCode != null && dtoAdminCode.equals(adminCode)) {
            role = UserRole.ADMIN;
        }

        String uuid = UUID.randomUUID().toString();

        User user = User.builder()
                .id(uuid)
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);

        return toResponseDto(user);
    }

    public UserResponseDto getUserById(String id) {
        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        return toResponseDto(user);
    }

    public DataResponseDto getAllUsers() {
        List<UserResponseDto> userResponseDtoList = userRepository.findAllByIsActiveTrue().stream()
                .map(this::toResponseDto).toList();
        return new DataResponseDto(userResponseDtoList);
    }

    @Transactional
    public UserResponseDto updateUser(String id, UserUpdateRequestDto requestDto) {
        String newUsername = requestDto.getNewUsername();
        String newPassword = requestDto.getNewPassword();

        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        if (StringUtils.hasText(newUsername)) user.updateUsername(newUsername);
        if (StringUtils.hasText(newPassword)) user.updatePassword(passwordEncoder.encode(newPassword));

        return toResponseDto(user);
    }

    @Transactional
    public void deleteUser(@Valid String id) {
        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        user.updateIsActive(false);
    }

    public UserSampleResponseDto getSamplesByUserId(String id) {
        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        List<Group> groupList = groupRepository.findByUserId(user.getId());

        List<SampleWithUpdateStatusDto> responseDtoList = sampleRepository.findAllByUserId(id).stream()
                .map(this::toSampleWithUpdateStatusDto)
                .toList();

        return UserSampleResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .groupIds(groupList.stream().map(Group::getId).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .samples(responseDtoList)
                .build();
    }

    private UserResponseDto toResponseDto(User user) {
        List<Group> groupList = groupRepository.findByUserId(user.getId());

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .groupIds(groupList.stream().map(Group::getId).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private SampleWithUpdateStatusDto toSampleWithUpdateStatusDto(Sample sample) {
        UserSimpleResponseDto userSimpleResponseDto = null;
        User user = null;

        if (sample.getUpdatedBy() != null) {
            user = userRepository.findById(sample.getUpdatedBy()).orElseThrow(
                    () -> new EntityNotFoundException("User not found")
            );

            userSimpleResponseDto = UserSimpleResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
        }

        List<LabelResponseDto> labelResponseDtoList = new ArrayList<>();
        for (String labelId : sample.getLabels()) {
            Label label = labelRepository.findById(labelId).orElse(null);
            if (label == null || !label.getIsActive()) continue;
            LabelResponseDto labelResponseDto = LabelResponseDto.builder()
                    .labelId(label.getId())
                    .labelName(label.getName())
                    .build();
            labelResponseDtoList.add(labelResponseDto);
        }

        Boolean isUpdated = (user != null) &&
                !sampleRepository.findRequestedSampleByUserId(user.getId(), sample.getSampleDataId()).isEmpty();

        return SampleWithUpdateStatusDto.builder()
                .id(sample.getId())
                .isUpdated(isUpdated)
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .sampleId(sample.getSampleDataId())
                .naturalQuestion(sample.getNaturalQuestion())
                .sqlQuery(sample.getSqlQuery())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .updatedBy(userSimpleResponseDto)
                .labels(labelResponseDtoList)
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .build();
    }
}
