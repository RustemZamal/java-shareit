package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest extends PageRequest {

    private final int from;

    public OffsetPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
        this.from = page;
    }

    public OffsetPageRequest(int page, int size) {
        super(page, size, Sort.unsorted());
        this.from = page;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
