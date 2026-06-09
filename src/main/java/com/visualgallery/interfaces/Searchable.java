package com.visualgallery.interfaces;

import java.util.List;

/**
 * Searchable - Interface for search functionality.
 *
 * Demonstrates: Interface (OOP Principle)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public interface Searchable<T> {

    /**
     * Searches for items matching the given keyword.
     *
     * @param keyword the search keyword
     * @return list of matching results
     */
    List<T> search(String keyword);

    /**
     * Searches with additional filter options.
     *
     * @param keyword    the search keyword
     * @param filterType additional filter (e.g., "PHOTO", "VIDEO", "ALL")
     * @return list of filtered results
     */
    List<T> searchWithFilter(String keyword, String filterType);
}
