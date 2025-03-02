package org.example.datalabelingtool.domain.groups.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.GroupCreateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupDataResponseDto;
import org.example.datalabelingtool.domain.groups.dto.GroupResponseDto;
import org.example.datalabelingtool.domain.groups.dto.GroupUpdateRequestDto;
import org.example.datalabelingtool.domain.groups.service.GroupService;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserUpdateRequestDto;
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
    public ResponseEntity<GroupDataResponseDto> getGroupById(@Valid @PathVariable String id) {
        GroupDataResponseDto responseDto = groupService.getGroupById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllGroups() {
        DataResponseDto responseDto = groupService.getAllGroups();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(@Valid @PathVariable String id,
                                                        @Valid @RequestBody GroupUpdateRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateGroup(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@Valid @PathVariable String id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    // add users

    // remove users

    // add samples

    // remove samples
}
