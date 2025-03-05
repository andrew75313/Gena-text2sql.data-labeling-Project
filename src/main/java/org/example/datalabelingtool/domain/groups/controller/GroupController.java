package org.example.datalabelingtool.domain.groups.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Create a new group",
            description = "Creates a new group with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Group created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping("")
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupCreateRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.createGroup(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get group by ID",
            description = "Fetches a group by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(@Valid @PathVariable String id) {
        GroupResponseDto responseDto = groupService.getGroupById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all groups",
            description = "Fetches all groups in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllGroups() {
        DataResponseDto responseDto = groupService.getAllGroups();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update a group",
            description = "Updates an existing group's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(@Valid @PathVariable String id, @Valid @RequestBody GroupUpdateRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateGroup(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a group",
            description = "Deletes an existing group by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Group deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@Valid @PathVariable String id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update group reviewers",
            description = "Updates the reviewers for a specific group.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reviewers updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    @PostMapping("/{id}/update-reviewers")
    public ResponseEntity<GroupResponseDto> updateReviewers(@Valid @PathVariable String id,
                                                            @Valid @RequestBody GroupUpdateReviewersRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateReviewers(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update group samples",
            description = "Updates the samples for a specific group.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Samples updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GroupResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Group not found")
            }
    )
    @PostMapping("/{id}/update-samples")
    public ResponseEntity<GroupResponseDto> updateSamples(@Valid @PathVariable String id,
                                                          @Valid @RequestBody GroupUpdateSamplesRequestDto requestDto) {
        GroupResponseDto responseDto = groupService.updateSamples(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
