package com.BE.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageUtil {
    public void checkOffset(int offset) {
        if (offset < 1) {
            throw new IllegalArgumentException("Offset must be greater than 0");
        }
    }

    public Pageable getPageable(int offset, int pageSize, String sortBy) {
        return sortBy == null
                ? PageRequest.of(offset, pageSize)
                : PageRequest.of(offset, pageSize, Sort.by(sortBy));
    }

}