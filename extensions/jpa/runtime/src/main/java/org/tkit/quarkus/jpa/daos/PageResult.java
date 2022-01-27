package org.tkit.quarkus.jpa.daos;

import java.util.stream.Stream;

/**
 * The page query result
 *
 * @param <T> the page results type
 */
public class PageResult<T> {

    /**
     * The total elements in the database.
     */
    private long totalElements;

    /**
     * The page number.
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
    private Stream<T> stream;

    /**
     * The default constructor.
     *
     * @param totalElements  the count of all items.
     * @param stream the data stream.
     * @param page   the page.
     */
    public PageResult(long totalElements, Stream<T> stream, Page page) {
        this.totalElements = totalElements;
        this.stream = stream;
        this.number = page.number();
        this.size = page.size();
        this.totalPages = (totalElements + size - 1) / size;
    }

    /**
     * Gets the number of the pages.
     *
     * @return the number of the pages.
     */
    public long getTotalPages() {
        return totalPages;
    }

    /**
     * Gets the size of the page.
     *
     * @return the size of the page.
     */
    public long getSize() {
        return size;
    }

    /**
     * Gets the index of the page.
     *
     * @return the index of the page.
     */
    public long getNumber() {
        return number;
    }

    /**
     * Gets the count of all items in the database.
     *
     * @return the count of all items in the database.
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * The stream of the data.
     *
     * @return the stream of the data.
     */
    public Stream<T> getStream() {
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PageResult{" +
                "c=" + totalElements +
                ",n=" + number +
                ",s=" + size +
                '}';
    }
}
