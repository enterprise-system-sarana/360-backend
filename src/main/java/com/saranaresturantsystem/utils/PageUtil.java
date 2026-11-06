package com.saranaresturantsystem.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public interface PageUtil {
    int DEFAULT_PAGE_SIZE = 10;
    int DEFAULT_PAGE_NUMBER = 1;

    String PAGE_LIMIT = "size";
    String PAGE_NUMBER = "page";

    static Pageable fromParams(Map<String, String> params) {
        return fromParams(params, Sort.unsorted());
    }

    static Pageable fromParams(Map<String, String> params, Sort defaultSort) {
        int page = DEFAULT_PAGE_NUMBER;
        int size = DEFAULT_PAGE_SIZE;

        try {
            if (params != null && params.containsKey(PAGE_NUMBER)) {
                page = Integer.parseInt(params.get(PAGE_NUMBER));
            }
            if (params != null && params.containsKey(PAGE_LIMIT)) {
                size = Integer.parseInt(params.get(PAGE_LIMIT));
            }
        } catch (NumberFormatException e) {
            page = DEFAULT_PAGE_NUMBER;
            size = DEFAULT_PAGE_SIZE;
        }

        return getPageable(page, size, defaultSort);
    }

    static Pageable getPageable(int page, int size) {
        return getPageable(page, size, Sort.unsorted());
    }

    static Pageable getPageable(int page, int size, Sort sort) {
        if (page < 1) {
            page = DEFAULT_PAGE_NUMBER;
        }
        if (size < 1) {
            size = DEFAULT_PAGE_SIZE;
        }

        if (sort != null && sort.isSorted()) {
            return PageRequest.of(page - 1, size, sort);
        }
        return PageRequest.of(page - 1, size);
    }
}