package com.quickInvoice.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceView {
    private String sellerName;
    private String sellerAddress;
    private String sellerGstin;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private double subtotal;
    private double tax;
    private double total;
    private List<InvoiceItem> items;
}