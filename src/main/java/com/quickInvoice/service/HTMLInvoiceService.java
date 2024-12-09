package com.quickInvoice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.quickInvoice.model.Invoice;
import com.quickInvoice.model.InvoiceItem;
import com.quickInvoice.model.InvoiceView;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class HTMLInvoiceService {
	
	@Autowired
    private TemplateEngine templateEngine;

    public byte[] generateInvoicePdf(Invoice invoice) throws Exception {
        // Prepare context for Thymeleaf
        Context context = new Context();
        context.setVariable("invoice", prepareInvoiceContext(invoice));

        // Render HTML template
        String htmlContent = templateEngine.process("invoice-template", context);

        // Convert HTML to PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    private InvoiceView prepareInvoiceContext(Invoice invoice) {
        return new InvoiceView(
            invoice.getSeller(),
            invoice.getSellerAddress(),
            invoice.getSellerGstin(),
            generateInvoiceNumber(),
            LocalDate.now(),
            calculateSubtotal(invoice.getItems()),
            calculateTax(invoice.getItems()),
            calculateTotal(invoice.getItems()),
            invoice.getItems()
        );
    }

    // Utility methods for calculations
    private double calculateSubtotal(List<InvoiceItem> items) {
        return items.stream()
            .mapToDouble(item -> item.getQuantity() * item.getRate())
            .sum();
    }

    private double calculateTax(List<InvoiceItem> items) {
        return calculateSubtotal(items) * 0.18; // 18% GST
    }

    private double calculateTotal(List<InvoiceItem> items) {
        return calculateSubtotal(items) + calculateTax(items);
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}