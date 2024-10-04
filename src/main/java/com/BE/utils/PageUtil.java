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

    public Pageable getPageable(int offset, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.unsorted();

        // Check if sortBy is provided
        if (sortBy != null) {
            // Check the direction (ascending or descending)
            sort = "desc".equalsIgnoreCase(sortDirection)
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }

        return PageRequest.of(offset, pageSize, sort);
    }


}