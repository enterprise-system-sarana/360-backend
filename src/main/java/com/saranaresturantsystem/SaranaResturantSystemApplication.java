package com.saranaresturantsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SaranaResturantSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaranaResturantSystemApplication.class, args);
    }

}
