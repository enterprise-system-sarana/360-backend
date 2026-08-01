package com.saranaresturantsystem.execption;
import lombok.*;
import org.springframework.http.HttpStatus;
@Getter
@Setter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
}
