package org.example.datalabelingtool.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.dto.UserCreateRequestDto;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserSampleResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserUpdateRequestDto;
import org.example.datalabelingtool.domain.users.service.UserService;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("")
    public ResponseEntity<UserResponseDto> createUser(@Parameter(description = "DTO for creating a new user") @Valid @RequestBody UserCreateRequestDto requestDto) {
        UserResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user from the system using their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Valid @PathVariable String id) {
        UserResponseDto responseDto = userService.getUserById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve samples for a specific user",
            description = "Fetches a list of samples associated with the given user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Samples retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserSampleResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}/samples")
    public ResponseEntity<UserSampleResponseDto> getSamplesByUserId(
            @Parameter(description = "ID of the user to retrieve samples for") @Valid @PathVariable String id) {
        UserSampleResponseDto responseDto = userService.getSamplesByUserId(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @GetMapping("")
    public ResponseEntity<DataResponseDto> getAllUsers() {
        DataResponseDto responseDto = userService.getAllUsers();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "ID of the user to update") @Valid @PathVariable String id,
            @Parameter(description = "User update request DTO") @Valid @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto responseDto = userService.updateUser(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to delete") @Valid @PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
