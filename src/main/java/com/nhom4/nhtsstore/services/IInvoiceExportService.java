package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.entities.Invoice;

import java.io.File;
import java.io.IOException;

/**
 * Service interface for exporting invoices to PDF format
 */
public interface IInvoiceExportService {

    /**
     * Exports an invoice to a PDF file
     * 
     * @param invoice The invoice to export
     * @param outputFile The file to write the PDF to
     * @throws IOException If there is an error writing to the file
     */
    void exportInvoiceToPdf(Invoice invoice, File outputFile) throws IOException;



    /**
     * Exports an invoice to a PDF file in the configured invoices directory
     * 
     * @param invoice The invoice to export
     * @return The generated PDF file
     * @throws IOException If there is an error creating the file
     */
    File exportInvoiceToPdfInInvoicesDir(Invoice invoice) throws IOException;

    /**
     * Gets the configured invoices directory
     * 
     * @return The invoices directory
     */
    File getInvoicesDirectory();

    /**
     * Finds an existing invoice PDF file by invoice ID
     * 
     * @param invoiceId The invoice ID
     * @return The existing file, or null if not found
     */
    File findExistingInvoicePdf(Long invoiceId);
}
