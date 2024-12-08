package com.quickInvoice.model;

import lombok.Data;

@Data
public class InvoiceItem {
    private String name;
    private String quantity;
    private double rate;
    private double amount;
}