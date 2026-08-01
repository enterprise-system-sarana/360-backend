package com.saranaresturantsystem.execption;


import org.springframework.http.HttpStatus;


public class InsufficientStockException extends ApiException {
    public InsufficientStockException(String productName, int required, int available) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("Insufficient stock for '%s': required %d, available %d",
                        productName, required, available));
    }
    public InsufficientStockException(int quantity) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("InsufficientStock %d", quantity));
    }
    public InsufficientStockException(String productName) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("Insufficient stock for '%s'" , productName));
    }
}
