package com.quickInvoice.controller;

import com.quickInvoice.model.Invoice;
import com.quickInvoice.model.InvoiceItem;
import com.quickInvoice.service.QuickInvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private QuickInvoiceService quickInvoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private Invoice testInvoice;

    @BeforeEach
    void setup() {
        testInvoice = new Invoice();
        testInvoice.setSeller("XYZ Pvt. Ltd.");
        testInvoice.setSellerGstin("29AABBCCDD121ZD");
        testInvoice.setSellerAddress("New Delhi, India");
        testInvoice.setBuyer("Vedant Computers");
        testInvoice.setBuyerGstin("29AABBCCDD131ZD");
        testInvoice.setBuyerAddress("New Delhi, India");

        List<InvoiceItem> items = new ArrayList<>();
        InvoiceItem item = new InvoiceItem();
        item.setName("Ponds Powder");
        item.setQuantity(12);
        item.setRate(45);
        item.setAmount(540);
        items.add(item);
        testInvoice.setItems(items);
    }

    @Test
    void generateInvoicePdf_success() throws IOException {
        byte[] pdfBytes = new byte[]{0x01, 0x02, 0x03};
        when(quickInvoiceService.generatePdf(testInvoice)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> response = invoiceController.generateInvoicePdf(testInvoice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pdfBytes, response.getBody());
    }

    @Test
    void generateInvoicePdf_ioException() throws IOException {
        when(quickInvoiceService.generatePdf(testInvoice)).thenThrow(new IOException("PDF generation failed"));

        ResponseEntity<byte[]> response = invoiceController.generateInvoicePdf(testInvoice);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}