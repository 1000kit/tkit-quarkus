package org.tkit.quarkus.jpa.daos;

/**
 * This class represents the page for query.
 */
public class Page {

    /**
     * Index of the page.
     */
    private int number;

    /**
     * Size of the page
     */
    private int size;

    /**
     * Default constructor.
     *
     * @param number the number of the page.
     * @param size  the size of the page.
     */
    private Page(int number, int size) {
        this.number = number;
        this.size = size;
    }

    /**
     * The page index.
     *
     * @return the page index.
     */
    public int number() {
        return number;
    }

    /**
     * Page size.
     *
     * @return the size of the page.
     */
    public int size() {
        return size;
    }

    /**
     * Creates page for the index and size.
     *
     * @param number the page number.
     * @param size  the page size.
     * @return the corresponding page.
     */
    public static Page of(int number, int size) {
        return new Page(number, size);
    }

    @Override
    public String toString() {
        return "Page{" +
                "n=" + number +
                ",s=" + size +
                '}';
    }
}
