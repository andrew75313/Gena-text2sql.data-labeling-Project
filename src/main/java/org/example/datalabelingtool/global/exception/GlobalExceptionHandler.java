package org.example.datalabelingtool.global.exception;

import jakarta.persistence.EntityNotFoundException;
import org.example.datalabelingtool.global.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(toErrorResponseDto("ValidationException", errorMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(toErrorResponseDto("NotFoundException", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleFileProcessingException(FileProcessingException ex) {
        return new ResponseEntity<>(toErrorResponseDto("FileProcessingException", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception e) {
        return new ResponseEntity<>(toErrorResponseDto(e.getClass().getSimpleName(),
                e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    private ErrorResponseDto toErrorResponseDto(String error, String errorMessage) {
        return ErrorResponseDto.builder()
                .error(error)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
