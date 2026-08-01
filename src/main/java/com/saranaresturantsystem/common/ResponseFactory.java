package com.saranaresturantsystem.common;

import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Data
public final class ResponseFactory {

    public static ResponseEntity<ApiResponse<PageDTO>> ok(Page<?> page, String entity) {
        return ResponseEntity.ok(
                ApiResponse.<PageDTO>builder()
                        .success(true)
                        .Instant(Instant.now())
                        .status(HttpStatus.OK)
                        .message(Message.getAll(entity))
                        .payload(new PageDTO(page))
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T payload, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .Instant(Instant.now())
                        .status(HttpStatus.OK)
                        .message(message)
                        .payload(payload)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T payload, String entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<T>builder()
                        .success(true)
                        .Instant(Instant.now())
                        .status(HttpStatus.CREATED)
                        .message(Message.created(entity))
                        .payload(payload)
                        .build()
        );
    }


    public static ResponseEntity<ApiResponse<Void>> deleted(String entity, Long id) {
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .Instant(Instant.now())
                        .status(HttpStatus.OK)
                        .message(Message.deleted(entity, id))
                        .build()
        );
    }
}
