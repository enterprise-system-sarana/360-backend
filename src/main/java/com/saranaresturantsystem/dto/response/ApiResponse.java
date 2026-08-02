package com.saranaresturantsystem.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Boolean success,
        HttpStatus status,
        String message,
        T payload,
        Instant Instant
) {

}
