# QuickInvoice

## Overview
QuickInvoice is a Spring Boot application that provides a REST API for dynamic PDF generation, specifically designed for creating invoices based on input data. The application uses iText for PDF generation and implements caching to avoid regenerating existing PDFs.

## Features
- Dynamic PDF generation from JSON input
- Local PDF storage and retrieval
- REST API endpoint for invoice PDF generation
- Unique PDF identification and caching
- Uses Courier Bold font for consistent styling
- Web-based invoice input form with direct PDF download

## Technology Stack
- Spring Boot
- Thymeleaf
- iText PDF Library
- Java 8+
- Maven

## Prerequisites
- Java 8 or higher
- Maven
- Postman (optional, for testing)

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com.quickinvoice/
│   │       ├── controller/
│   │       │   ├── InvoiceController.java
│   │       │   └── InvoiceWebController.java
│   │       ├── model/
│   │       │   ├── Invoice.java
│   │       │   └── InvoiceItem.java
│   │       ├── dao/
│   │       │   └── QuickInvoiceRepository.java
│   │       ├── service/
│   │       │   └── QuickInvoiceService.java
│   │       └── QuickInvoiceApplication.java
│   └── resources/
│       ├── templates/
│       │       └── invoice-form.html
│       └── application.properties
└── test/
    └── java/
        └── com.quickinvoice/
```

## Installation

1. Clone the repository:
```
git clone https://github.com/vivekanand-vr/quickInvoice.git
cd quickInvoice
```

2. Build the project:
```
mvn clean install
```

3. Run the application:
```
mvn spring-boot:run
```

## API Endpoint

### Generate PDF
- **URL:** `/api/generate`
- **Method:** POST
- **Content-Type:** application/json

#### Request Body Example
```json
{
    "seller": "XYZ Pvt. Ltd.",
    "sellerGstin": "29AABBCCDD121ZD",
    "sellerAddress": "New Delhi, India",
    "buyer": "Vedant Computers", 
    "buyerGstin": "29AABBCCDD131ZD",
    "buyerAddress": "New Delhi, India",
    "items": [
        {
            "name": "Product 1",
            "quantity": "12 Nos",
            "rate": 123.00,
            "amount": 1476.00
        }
    ]
}
```

## Generate PDF from Web

- URL: `/invoice/generate`
- Method: POST
- Content-Type: application/x-www-form-urlencoded

## Testing
- Use Postman to send POST requests to the endpoint.
- Test the web-based invoice generation form at `/invoice/create`
- Verify PDF generation and storage

## Error Handling
- Returns HTTP 500 for internal server errors
- Logs exceptions for debugging

## Performance Considerations
- Caches PDFs to avoid regeneration
- Uses efficient byte stream processing

## Future Improvements
- Add more robust error handling
- Implement PDF template customization
- Add more comprehensive logging
