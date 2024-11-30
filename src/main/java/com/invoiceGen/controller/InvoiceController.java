package com.invoiceGen.controller;

import com.invoiceGen.model.Invoice;
import com.invoiceGen.service.PdfGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class InvoiceController {

    @Autowired
    private PdfGenerationService pdfGenerationService;

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
}