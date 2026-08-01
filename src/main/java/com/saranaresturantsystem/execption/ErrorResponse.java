package com.saranaresturantsystem.execption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private  final HttpStatus status ;
    private  final String message ;
}
