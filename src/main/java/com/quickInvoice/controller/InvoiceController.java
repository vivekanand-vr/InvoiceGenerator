package com.quickInvoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quickInvoice.model.Invoice;
import com.quickInvoice.service.QuickInvoiceService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @Autowired
    private QuickInvoiceService pdfGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateInvoicePdf(@RequestBody Invoice invoiceRequest) {
        try {
            byte[] pdfBytes = pdfGenerationService.generatePdf(invoiceRequest);

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "invoice.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generate-from-web")
    public ResponseEntity<byte[]> generateInvoicePdfFromWeb(@ModelAttribute Invoice invoiceRequest) {
        try {
            // Remove any empty items
            invoiceRequest.setItems(invoiceRequest.getItems().stream()
                .filter(item -> item.getName() != null && !item.getName().isEmpty())
                .toList());

            byte[] pdfBytes = pdfGenerationService.generatePdf(invoiceRequest);

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "invoice.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}