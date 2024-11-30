# Invoice Generator Application

## Overview
This Spring Boot application provides a REST API for dynamic PDF generation, specifically designed for creating invoices based on input data. The application uses iText for PDF generation and implements caching to avoid regenerating existing PDFs.

## Features
- Dynamic PDF generation from JSON input
- Local PDF storage and retrieval
- REST API endpoint for invoice PDF generation
- Unique PDF identification and caching
- Uses Courier Bold font for consistent styling

## Technology Stack
- Spring Boot
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
│   │   └── com.invoiceGen/
│   │       ├── controller/
│   │       │   └── InvoiceController.java
│   │       ├── model/
│   │       │   ├── Invoice.java
│   │       │   └── InvoiceItem.java
│   │       ├── dao/
│   │       │   └── PdfStorageRepository.java
│   │       ├── service/
│   │       │   └── PdfGenerationService.java
│   │       └── InvoiceGeneratorApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com.invoiceGen/
```

## Installation

1. Clone the repository:
```
git clone https://github.com/vivekanand-vr/invoiceGenerator.git
cd invoiceGenerator
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
- **URL:** `/api/pdf/generate`
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

## Testing
- Use Postman to send POST requests to the endpoint
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
