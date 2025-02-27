package org.example.datalabelingtool.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponseDto {
    private String error;
    private String errorMessage;
    private LocalDateTime timestamp;
}
