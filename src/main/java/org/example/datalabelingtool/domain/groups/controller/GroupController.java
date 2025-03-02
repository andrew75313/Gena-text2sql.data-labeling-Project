package org.example.datalabelingtool.domain.groups.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.dto.GroupCreateRequestDto;
import org.example.datalabelingtool.domain.groups.dto.GroupResponseDto;
import org.example.datalabelingtool.domain.groups.service.GroupService;
import org.example.datalabelingtool.domain.users.dto.UserCreateRequestDto;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // update group

    // retrieve group

    // retrieve all group

    // delete group

    // add users

    // remove users

    // add samples

    // remove samples
}
