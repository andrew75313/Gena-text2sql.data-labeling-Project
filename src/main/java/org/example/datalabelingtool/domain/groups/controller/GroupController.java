package org.example.datalabelingtool.domain.groups.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.*;
import org.example.datalabelingtool.domain.groups.service.GroupService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping("")
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupCreateRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.createGroup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(@Valid @PathVariable String id) {
        GroupResponseDto responseDto = groupService.getGroupById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllGroups() {
        DataResponseDto responseDto = groupService.getAllGroups();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(@Valid @PathVariable String id, @Valid @RequestBody GroupUpdateRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateGroup(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@Valid @PathVariable String id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-reviewers")
    public ResponseEntity<GroupResponseDto> updateReviewers(@Valid @PathVariable String id,
                                                            @Valid @RequestBody GroupUpdateReviewersRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateReviewers(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/update-samples")
    public ResponseEntity<GroupResponseDto> updateSamples(@Valid @PathVariable String id,
                                                            @Valid @RequestBody GroupUpdateSamplesRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateSamples(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
