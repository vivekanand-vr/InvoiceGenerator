package com.quickInvoice.model;

import lombok.Data;

@Data
public class InvoiceItem {
    private String name;
    private int quantity;
    private double rate;
    private double amount;
}