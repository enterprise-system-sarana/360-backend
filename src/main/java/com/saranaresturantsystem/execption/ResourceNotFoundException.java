package com.saranaresturantsystem.execption;

import org.springframework.http.HttpStatus;


public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resourceName , Long id ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s  Id = %d not found" ,resourceName , id ));
    }
    public ResourceNotFoundException(String resourceName , Long id  , Long Id) {
        super(HttpStatus.NOT_FOUND,  String.format("%s  Id = %d not found" ,resourceName , id  , Id ));
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
