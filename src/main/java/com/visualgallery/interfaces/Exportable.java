package com.visualgallery.interfaces;

import java.io.File;

/**
 * Exportable - Interface for data export functionality.
 *
 * Demonstrates: Interface (OOP Principle)
 *
 * Classes implementing this interface can export their data
 * to PDF or Excel formats.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public interface Exportable {

    /**
     * Exports data to a PDF file.
     *
     * @param outputFile the destination PDF file
     * @return true if export was successful
     */
    boolean exportToPdf(File outputFile);

    /**
     * Exports data to an Excel file.
     *
     * @param outputFile the destination Excel (.xlsx) file
     * @return true if export was successful
     */
    boolean exportToExcel(File outputFile);

    /**
     * Gets a descriptive title for the exported data.
     *
     * @return the export title
     */
    String getExportTitle();
}
