package com.quickInvoice.model;

import java.util.List;
import lombok.Data;

@Data
public class Invoice {
    private String seller;
    private String sellerGstin;
    private String sellerAddress;
    private String buyer;
    private String buyerGstin;
    private String buyerAddress;
    private List<InvoiceItem> items;
}