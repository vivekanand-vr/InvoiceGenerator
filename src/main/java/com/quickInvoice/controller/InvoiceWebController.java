package com.quickInvoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quickInvoice.model.Invoice;
import com.quickInvoice.service.HTMLInvoiceService;
import java.io.IOException;

@Controller
@RequestMapping("/invoice")
public class InvoiceWebController {
  
    @Autowired
    private HTMLInvoiceService htmlInvoiceService;

    @GetMapping("/create")
    public String showInvoiceForm(Model model) {
        // Create an invoice with initial empty items
        Invoice invoice = new Invoice();
        
        model.addAttribute("invoice", invoice);
        return "invoice-form";
    }

    @PostMapping("/html")
    public ResponseEntity<byte[]> generateInvoice(@ModelAttribute("invoice") Invoice invoice) throws Exception {
        try {
            // Remove any empty items
            invoice.setItems(invoice.getItems().stream()
                .filter(item -> item.getName() != null && !item.getName().isEmpty())
                .toList());
            
            // Generate PDF
            byte[] pdfBytes = htmlInvoiceService.generateInvoicePdf(invoice);

            // Prepare HTTP headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                org.springframework.http.ContentDisposition.attachment().filename("invoice.pdf").build()
            );
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}