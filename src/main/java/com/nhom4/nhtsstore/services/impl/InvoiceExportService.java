package com.nhom4.nhtsstore.services.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.nhom4.nhtsstore.entities.Invoice;
import com.nhom4.nhtsstore.entities.InvoiceDetail;
import com.nhom4.nhtsstore.services.IInvoiceExportService;
import com.nhom4.nhtsstore.services.IInvoiceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class InvoiceExportService implements IInvoiceExportService {
    private final IInvoiceService invoiceService;
    private static Font TITLE_FONT;
    private static Font HEADER_FONT;
    private static Font NORMAL_FONT;
    private static Font BOLD_FONT;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###,###");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        try {

            BaseFont latoRegular = BaseFont.createFont(
                new ClassPathResource("fonts/Vietnamese Lato/Lato Regular.ttf").getFile().getAbsolutePath(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
            );

            BaseFont latoBold = BaseFont.createFont(
                new ClassPathResource("fonts/Vietnamese Lato/Lato Bold.ttf").getFile().getAbsolutePath(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
            );

            TITLE_FONT = new Font(latoBold, 18);
            HEADER_FONT = new Font(latoBold, 12);
            NORMAL_FONT = new Font(latoRegular, 10);
            BOLD_FONT = new Font(latoBold, 10);

        } catch (Exception e) {
            // Fallback to default fonts if Vietnamese fonts can't be loaded
            TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            BOLD_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            System.err.println("Error loading Vietnamese fonts: " + e.getMessage());
        }
    }
    public InvoiceExportService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    @Value("${app.invoice.export.directory}")
    private String invoiceExportDirectory;



    @Override
    public void exportInvoiceToPdf(Invoice invoice, File outputFile) throws IOException {
        invoice = invoiceService.findById(invoice.getId());
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            // Add logo
            try {
                Image logo = Image.getInstance(new ClassPathResource("NHTS_Store_logo_64x64.png").getFile().getAbsolutePath());
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(64, 64);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
            }

            //Title
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            //invoice number and date
            document.add(new Paragraph("Số hóa đơn: " + invoice.getId(), HEADER_FONT));
            document.add(new Paragraph("Ngày: " + (invoice.getLastModifiedOn() != null ?
                    invoice.getLastModifiedOn().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) :
                    (invoice.getCreateDate() != null ? DATE_FORMAT.format(invoice.getCreateDate()) : "")), HEADER_FONT));
            document.add(Chunk.NEWLINE);

            //seller information
            document.add(new Paragraph("THÔNG TIN NGƯỜI BÁN:", HEADER_FONT));
            document.add(new Paragraph("Tên cửa hàng: NHTS Store", NORMAL_FONT));
            document.add(new Paragraph("Địa chỉ: Khu phố 6, P.Linh Trung, Tp.Thủ Đức, Tp.Hồ Chí Minh", NORMAL_FONT));
            document.add(new Paragraph("Mã số thuế: 0123456789", NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            //buyer information
            document.add(new Paragraph("THÔNG TIN NGƯỜI MUA:", HEADER_FONT));

            // Handle null customer safely
            if (invoice.getCustomer() != null) {
                document.add(new Paragraph("Tên khách hàng: " + invoice.getCustomer().getName(), NORMAL_FONT));
            } else {
                document.add(new Paragraph("Tên khách hàng: [Không có thông tin]", NORMAL_FONT));
            }

            if (invoice.getPhoneNumber() != null && !invoice.getPhoneNumber().isEmpty()) {
                document.add(new Paragraph("Số điện thoại: " + invoice.getPhoneNumber(), NORMAL_FONT));
            }

            // Add fulfillment method info
            if (invoice.getFulfilmentMethod() != null) {
                document.add(new Paragraph("Phương thức nhận hàng: " + invoice.getFulfilmentMethod().getDisplayName(), NORMAL_FONT));
            }

            // Only show delivery address for non-pickup orders
            boolean isPickup = invoice.getFulfilmentMethod() != null &&
                    invoice.getFulfilmentMethod().toString().equals("PICKUP");

            if (!isPickup && invoice.getDeliveryAddress() != null && !invoice.getDeliveryAddress().isEmpty()) {
                document.add(new Paragraph("Địa chỉ giao hàng: " + invoice.getDeliveryAddress(), NORMAL_FONT));
            }

            document.add(Chunk.NEWLINE);

            // Add invoice details table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 1, 2, 2});

            // table header
            addTableHeader(table);

            // table data
            addTableData(table, invoice);

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Add subtotal, delivery fee and total amount
            Paragraph subtotal = new Paragraph("Tổng tiền hàng: " +
                    invoice.getTotalAmount() + " USD", NORMAL_FONT);
            subtotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(subtotal);

            // Add delivery fee if not pickup
            if (!isPickup && invoice.getDeliveryFee() != null) {
                Paragraph deliveryFee = new Paragraph("Phí vận chuyển: " +
                        invoice.getDeliveryFee() + " USD", NORMAL_FONT);
                deliveryFee.setAlignment(Element.ALIGN_RIGHT);
                document.add(deliveryFee);

                // Calculate and display grand total
                BigDecimal grandTotal = invoice.getTotalAmount().add(invoice.getDeliveryFee());
                Paragraph totalAmount = new Paragraph("Tổng cộng: " +
                        grandTotal+ " USD", BOLD_FONT);
                totalAmount.setAlignment(Element.ALIGN_RIGHT);
                document.add(totalAmount);
            } else {
                // For pickup, just show the total amount as grand total
                Paragraph totalAmount = new Paragraph("Tổng cộng: " +
                        invoice.getTotalAmount() + " USD", BOLD_FONT);
                totalAmount.setAlignment(Element.ALIGN_RIGHT);
                document.add(totalAmount);
            }

            // Add payment information
            if (invoice.getPaymentMethod() != null) {
                document.add(new Paragraph("Phương thức thanh toán: " + invoice.getPaymentMethod().getDisplayName(), NORMAL_FONT));
            }
            if (invoice.getPaymentStatus() != null) {
                document.add(new Paragraph("Trạng thái thanh toán: " + invoice.getPaymentStatus().getDisplayName(), NORMAL_FONT));
            }
            if (invoice.getPaymentTransactionId() != null && !invoice.getPaymentTransactionId().isEmpty()) {
                document.add(new Paragraph("Mã giao dịch: " + invoice.getPaymentTransactionId(), NORMAL_FONT));
            }

            // Add signature section
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);

            PdfPCell buyerCell = new PdfPCell(new Phrase("Người mua hàng\n(Ký, ghi rõ họ tên)", NORMAL_FONT));
            buyerCell.setBorder(Rectangle.NO_BORDER);
            buyerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            buyerCell.setPaddingBottom(50);

            PdfPCell sellerCell = new PdfPCell(new Phrase("Người bán hàng\n(Ký, ghi rõ họ tên)", NORMAL_FONT));
            sellerCell.setBorder(Rectangle.NO_BORDER);
            sellerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sellerCell.setPaddingBottom(50);

            signatureTable.addCell(buyerCell);
            signatureTable.addCell(sellerCell);

            document.add(signatureTable);

            // Add footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã mua hàng tại NHTS Store!", NORMAL_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

        } catch (DocumentException e) {
            throw new IOException("Error creating PDF document", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }



    @Override
    public File exportInvoiceToPdfInInvoicesDir(Invoice invoice) throws IOException {
        // Create the invoices directory if it doesn't exist
        File invoicesDir = getInvoicesDirectory();
        if (!invoicesDir.exists()) {
            invoicesDir.mkdirs();
        }

        // Create a file in the invoices directory
        String fileName = "invoice_" + invoice.getId() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        File invoiceFile = new File(invoicesDir, fileName);

        exportInvoiceToPdf(invoice, invoiceFile);

        return invoiceFile;
    }

    @Override
    public File getInvoicesDirectory() {
        return new File(invoiceExportDirectory);
    }


    @Override
    public File findExistingInvoicePdf(Long invoiceId) {
        // Get the invoices directory
        File invoicesDir = getInvoicesDirectory();
        if (!invoicesDir.exists()) {
            return null;
        }

        // Get all files in the directory
        File[] files = invoicesDir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        // Filter files that match the pattern "invoice_[invoiceId]_*.pdf"
        String prefix = "invoice_" + invoiceId + "_";
        File mostRecentFile = null;
        long mostRecentTime = 0;

        for (File file : files) {
            if (file.isFile() && file.getName().startsWith(prefix) && file.getName().endsWith(".pdf")) {
                // Check if this file is more recent than the current most recent file
                if (file.lastModified() > mostRecentTime) {
                    mostRecentFile = file;
                    mostRecentTime = file.lastModified();
                }
            }
        }

        return mostRecentFile;
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"STT", "Sản phẩm", "SL", "Đơn giá", "Thành tiền"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, BOLD_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableData(PdfPTable table, Invoice invoice) {
        int index = 1;

        for (InvoiceDetail detail : invoice.getInvoiceDetail()) {
            // Index
            PdfPCell indexCell = new PdfPCell(new Phrase(String.valueOf(index++), NORMAL_FONT));
            indexCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            indexCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(indexCell);

            // Product name
            String productName = "Unknown Product";
            if (detail.getProduct() != null && detail.getProduct().getName() != null && !detail.getProduct().getName().isEmpty()) {
                productName = detail.getProduct().getName();
            }
            PdfPCell productCell = new PdfPCell(new Phrase(productName, NORMAL_FONT));
            productCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(productCell);

            // Quantity
            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(detail.getQuantity()), NORMAL_FONT));
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(quantityCell);

            // Unit price
            PdfPCell priceCell = new PdfPCell(new Phrase(detail.getUnitPrice()+ " USD", NORMAL_FONT));
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(priceCell);

            // Subtotal
            PdfPCell subtotalCell = new PdfPCell(new Phrase(detail.getSubtotal()+ " USD", NORMAL_FONT));
            subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            subtotalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(subtotalCell);
        }
    }
}
