package com.saranaresturantsystem.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Map;

public interface PageUtil {
    int DEFAULT_PAGE_SIZE = 10;
    int DEFAULT_PAGE_NUMBER = 1;

    String PAGE_LIMIT = "size";
    String PAGE_NUMBER = "page";


    static Pageable fromParams(Map<String, String> params) {
        int page = DEFAULT_PAGE_NUMBER;
        int size = DEFAULT_PAGE_SIZE;

        try {
            if (params.containsKey(PAGE_NUMBER)) {
                page = Integer.parseInt(params.get(PAGE_NUMBER));
            }
            if (params.containsKey(PAGE_LIMIT)) {
                size = Integer.parseInt(params.get(PAGE_LIMIT));
            }
        } catch (NumberFormatException e) {
            page = DEFAULT_PAGE_NUMBER;
            size = DEFAULT_PAGE_SIZE;
        }

        return getPageable(page, size);
    }


    static Pageable getPageable(int page, int size) {
        if (page < 1) {
            page = DEFAULT_PAGE_NUMBER;
        }
        if (size < 1) {
            size = DEFAULT_PAGE_SIZE;
        }

        return PageRequest.of(page - 1, size);
    }
}