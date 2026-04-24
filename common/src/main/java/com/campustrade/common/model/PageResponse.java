package com.campustrade.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic page response wrapper.
 *
 * @param <T> record type
 */
public class PageResponse<T> implements Serializable {
    private long total;
    private int pageNum;
    private int pageSize;
    private int totalPages;
    private List<T> records;

    public PageResponse() {
        this.records = new ArrayList<>();
    }

    public PageResponse(long total, int pageNum, int pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(total, pageSize);
        this.records = records == null ? new ArrayList<>() : records;
    }

    public static <T> PageResponse<T> of(long total, int pageNum, int pageSize, List<T> records) {
        return new PageResponse<>(total, pageNum, pageSize, records);
    }

    public static <T> PageResponse<T> empty(int pageNum, int pageSize) {
        return new PageResponse<>(0, pageNum, pageSize, Collections.emptyList());
    }

    private int calculateTotalPages(long total, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) ((total + pageSize - 1) / pageSize);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        this.totalPages = calculateTotalPages(this.total, this.pageSize);
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(this.total, this.pageSize);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
