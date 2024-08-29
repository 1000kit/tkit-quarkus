package org.tkit.quarkus.rs.models;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @param <T> model
 * @deprecated will be removed in next major release.
 */
@Deprecated(since = "1.0.0")
@RegisterForReflection
@SuppressWarnings("java:S1133")
public class PageResultDTO<T> {

    /**
     * The total elements in the database.
     */
    private long totalElements;

    /**
     * The page index.
     */
    private int number;

    /**
     * The page size.
     */
    private int size;

    /**
     * The number of pages.
     */
    private long totalPages;

    /**
     * The data stream.
     */
    private List<T> stream;

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getStream() {
        return stream;
    }

    public void setStream(List<T> stream) {
        this.stream = stream;
    }
}
