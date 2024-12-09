package com.quickInvoice.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.quickInvoice.dao.QuickInvoiceRepository;
import com.quickInvoice.model.Invoice;
import com.quickInvoice.model.InvoiceItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class QuickInvoiceService {

    @Autowired
    private QuickInvoiceRepository pdfStorageRepository;

    @Value("${company.logo.path}")  // You can configure this in application.properties
    private String companyLogoPath;

    public byte[] generatePdf(Invoice invoice) throws IOException {
        // Existing hash generation and caching logic
        String pdfHash = pdfStorageRepository.generateUniqueHash(invoice);
        String existingFilename = pdfHash + ".pdf";
        
        if (pdfStorageRepository.pdfExists(existingFilename)) {
            return pdfStorageRepository.retrievePdf(existingFilename);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);     
            Document doc = new Document(pdfDoc);
            
            // Set Document-wide Styling
            PdfFont regularFont = PdfFontFactory.createFont();
            PdfFont boldFont = PdfFontFactory.createFont();
            
            // Invoice Header with Company Logo
            Table headerTable = createInvoiceHeader(invoice, companyLogoPath);
            doc.add(headerTable);

            // Seller and Buyer Information
            Table partyDetailsTable = createPartyDetailsTable(invoice);
            doc.add(partyDetailsTable);

            // Invoice Details (Number, Date)
            Paragraph invoiceDetails = createInvoiceDetailsSection(invoice);
            doc.add(invoiceDetails);

            // Product/Service Table
            Table productTable = createProductTable(invoice);
            doc.add(productTable);

            // Calculations Section
            Table calculationsTable = createCalculationsTable(invoice);
            doc.add(calculationsTable);

            // Footer (Optional: Add Notes, Payment Terms, etc.)
            Paragraph footer = createInvoiceFooter();
            doc.add(footer);

            writer.close();
            pdfDoc.close();
            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        
        byte[] pdfBytes = outputStream.toByteArray();
        pdfStorageRepository.storePdf(pdfBytes);

        return pdfBytes;
    }

    private Table createInvoiceHeader(Invoice invoice, String logoPath) throws MalformedURLException {
        float[] headerColumnWidths = { 200f, 400f };
        Table headerTable = new Table(headerColumnWidths);
        
        // Add Company Logo
        if (logoPath != null && !logoPath.isEmpty()) {
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setWidth(100);
            headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));
        } else {
            headerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }

        // Company Name and Contact Details
        Paragraph companyInfo = new Paragraph(invoice.getSeller())
            .setFontSize(16)
            .setBold();
        Paragraph companyAddress = new Paragraph(invoice.getSellerAddress());
        Paragraph companyGstin = new Paragraph("GSTIN: " + invoice.getSellerGstin());
        
        Cell companyCell = new Cell()
            .add(companyInfo)
            .add(companyAddress)
            .add(companyGstin)
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
        
        headerTable.addCell(companyCell);
        
        return headerTable;
    }

    private Table createPartyDetailsTable(Invoice invoice) {
        float[] partyColumnWidths = { 280f, 280f };
        Table partyTable = new Table(partyColumnWidths);
        
        Cell sellerCell = new Cell()
            .add(new Paragraph("Seller Details").setBold())
            .add(new Paragraph(invoice.getSeller()))
            .add(new Paragraph(invoice.getSellerAddress()))
            .add(new Paragraph("GSTIN: " + invoice.getSellerGstin()));
        
        Cell buyerCell = new Cell()
            .add(new Paragraph("Buyer Details").setBold())
            .add(new Paragraph(invoice.getBuyer()))
            .add(new Paragraph(invoice.getBuyerAddress()))
            .add(new Paragraph("GSTIN: " + invoice.getBuyerGstin()));
        
        partyTable.addCell(sellerCell).addCell(buyerCell);
        return partyTable;
    }

    private Paragraph createInvoiceDetailsSection(Invoice invoice) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String invoiceNumber = "INV-" + System.currentTimeMillis(); // Generate unique invoice number
        String invoiceDate = dateFormat.format(new Date());
        
        return new Paragraph("Invoice No: " + invoiceNumber + " | Date: " + invoiceDate)
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontSize(10);
    }

    private Table createProductTable(Invoice invoice) {
        float[] productColumnWidths = { 200f, 100f, 100f, 150f };
        Table productTable = new Table(productColumnWidths);
        productTable.setTextAlignment(TextAlignment.CENTER);
        
        // Table Headers
        String[] headers = {"Item Description", "Quantity", "Rate", "Amount"};
        for (String header : headers) {
            productTable.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }
        
        // Product Rows
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (InvoiceItem item : invoice.getItems()) {
            productTable.addCell(new Cell().add(new Paragraph(item.getName())));
            productTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            productTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(item.getRate()))));
            productTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(item.getAmount()))));
        }
        
        return productTable;
    }

    private Table createCalculationsTable(Invoice invoice) {
        float[] calculationColumnWidths = { 400f, 150f };
        Table calculationsTable = new Table(calculationColumnWidths);
        
        double subtotal = invoice.getItems().stream()
            .mapToDouble(InvoiceItem::getAmount)
            .sum();
        
        double taxRate = 0.18; // 18% GST example
        double taxAmount = subtotal * taxRate;
        double total = subtotal + taxAmount;
        
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        
        calculationsTable.addCell(new Cell().add(new Paragraph("Subtotal")).setTextAlignment(TextAlignment.RIGHT));
        calculationsTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(subtotal))).setTextAlignment(TextAlignment.RIGHT));
        
        calculationsTable.addCell(new Cell().add(new Paragraph("GST (18%)")).setTextAlignment(TextAlignment.RIGHT));
        calculationsTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(taxAmount))).setTextAlignment(TextAlignment.RIGHT));
        
        calculationsTable.addCell(new Cell().add(new Paragraph("Total")).setBold().setTextAlignment(TextAlignment.RIGHT));
        calculationsTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(total))).setBold().setTextAlignment(TextAlignment.RIGHT));
        
        return calculationsTable;
    }

    private Paragraph createInvoiceFooter() {
        return new Paragraph("Thank you for your business!")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10)
            .setItalic();
    }
}