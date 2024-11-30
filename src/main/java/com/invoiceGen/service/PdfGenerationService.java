package com.invoiceGen.service;

import com.invoiceGen.model.Invoice;
import com.invoiceGen.model.InvoiceItem;
import com.invoiceGen.dao.PdfStorageRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    @Autowired
    private PdfStorageRepository pdfStorageRepository;

    public byte[] generatePdf(Invoice invoice) throws IOException {
        // Generate unique hash to check if PDF already exists
        String pdfHash = pdfStorageRepository.generateUniqueHash(invoice);

        // Check if PDF already exists
        String existingFilename = pdfHash + ".pdf";
        if (pdfStorageRepository.pdfExists(existingFilename)) {
            return pdfStorageRepository.retrievePdf(existingFilename);
        }

        // Create PDF if not exists
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);     
            Document doc = new Document(pdfDoc);
            
            float[] headerColumnWidths = { 280F, 280F };
            
            // creating cells for seller and buyer
            Table header = new Table(headerColumnWidths);
            
            String seller = "Seller:\n" + 
                            invoice.getSeller() + "\n" + 
                            invoice.getSellerAddress() + "\n GSTIN: " +
                            invoice.getSellerGstin();
            
            header.addCell(new Cell().add(new Paragraph(seller)).setPadding(30));
            
            String buyer = "Buyer:\n" + 
                           invoice.getBuyer() + "\n" + 
                           invoice.getBuyerAddress() + "\n GSTIN: " +
                           invoice.getBuyerGstin();
            
            header.addCell(new Cell().add(new Paragraph(buyer)).setPadding(30));
            
            // creating table for products
            float[] productInfoColumnWidths = { 140, 140, 140, 140 };
            
            Table productInfoTable = new Table(productInfoColumnWidths);
            
            productInfoTable.setTextAlignment(TextAlignment.CENTER);
            productInfoTable.addCell(new Cell().add(new Paragraph("Item")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Quantity")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Rate")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Amount")));

            for (InvoiceItem item : invoice.getItems()) {
                productInfoTable.addCell(new Cell().add(new Paragraph(item.getName())));
                productInfoTable.addCell(new Cell().add(new Paragraph(item.getQuantity())));
                productInfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate()))));
                productInfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount()))));
            }
            
            doc.add(header);
            doc.add(productInfoTable);
            
            writer.close();
            pdfDoc.close();
            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        
        // Convert to byte array
        byte[] pdfBytes = outputStream.toByteArray();

        // Store PDF
        pdfStorageRepository.storePdf(pdfBytes);

        return pdfBytes;
    }
}