package org.example.datalabelingtool.domain.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.dto.UserCreateRequestDto;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserUpdateRequestDto;
import org.example.datalabelingtool.domain.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto requestDto) {

        UserResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Valid @PathVariable String id) {

        UserResponseDto responseDto = userService.getUserById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto requestDto) {

        UserResponseDto responseDto = userService.updateUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
