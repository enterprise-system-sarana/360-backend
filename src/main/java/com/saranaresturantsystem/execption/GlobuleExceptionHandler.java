package com.saranaresturantsystem.execption;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
@RestControllerAdvice
@ControllerAdvice
public class GlobuleExceptionHandler {
    // check api response
    @ExceptionHandler(value =  ApiException.class)
    public ResponseEntity<?> handleApiExecption(ApiException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus() , e.getMessage());
        return  ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    // validation method or field not null
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // check duplication or unuiqe field
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateException(DuplicateResourceException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.CONFLICT.value());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    // Foreign Key Constraint / Integrity Violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String msg = "Cannot delete or modify this resource because it is still referenced by other data.";
        return buildErrorResponse(HttpStatus.CONFLICT, msg);
    }
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceeded(
//            MaxUploadSizeExceededException ex) {
//
//        ApiResponse<?> response = ApiResponse.builder()
//                .succeess(false)
//                .status(HttpStatus.PAYLOAD_TOO_LARGE)
//                .message("File size exceeds the maximum allowed limit of 100MB")
//                .Instant(Instant.now())
//                .build();
//
//        return ResponseEntity
//                .status(HttpStatus.PAYLOAD_TOO_LARGE)
//                .body(response);
//    }

}
