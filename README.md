# QuickInvoice

## Overview
QuickInvoice is a Spring Boot application that provides a REST API for dynamic PDF generation, specifically designed for creating invoices based on input data. The application uses iText for PDF generation and implements caching to avoid regenerating existing PDFs.

## Features
- Dynamic PDF generation from JSON input
- Local PDF storage and retrieval
- REST API endpoint for invoice PDF generation
- Web-based invoice input form with direct PDF download
- Unique PDF identification and caching
- Uses Courier Bold font for consistent styling

### Technology Stack
- Spring Boot, Thymeleaf, iText PDF Library, Java 8+, JUnit, Maven

### Prerequisites
- Java 8 or higher, Maven, Postman (optional, for testing)

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
│   │       │   ├── InvoiceItem.java
│   │       │   └── InvoiceView.java
│   │       ├── dao/
│   │       │   └── QuickInvoiceRepository.java
│   │       ├── service/
│   │       │   ├── HTMLInvoiceService.java
│   │       │   └── QuickInvoiceService.java
│   │       └── QuickInvoiceApplication.java
│   └── resources/
│       ├── assets/
│       │       └── company-logo.png
│       ├── templates/
│       │       |── invoice-form.html
│       │       └── invoice-template.html
│       └── application.properties
└── test/
    └── java/
        └── com.quickinvoice/
            └── controller/
                └── InvoiceControllerTest.java

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

## API Endpoints

### Generate PDF
- **URL:**  `/invoice/pdf`
- **Method:** POST
- **Content-Type:** application/json
- Generates PDF manually using iText utility classes

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
            "quantity": 12,
            "rate": 123.00,
            "amount": 1476.00
        }
    ]
}
```

## Generate PDF from Web

- URL: `/invoice/create`
- Method: POST
- Internally Submits to `/invoice/html`
- Content-Type: application/x-www-form-urlencoded
- Generates PDF using the `invoice-template.html` as template and converting it into PDF format.

### Snapshot of Web UI
![web-view](https://github.com/user-attachments/assets/dafa5476-208d-4e5d-922a-7c0d8cbb313f)


## Testing

The QuickInvoice application includes a comprehensive test suite to ensure the reliability and stability of the PDF generation functionality. The test cases cover the following scenarios:

- Successful PDF generation
- Error handling for PDF generation failures
- Mocking of external dependencies

To run the tests, use the following command:
```
mvn test
```
The test results will be displayed in the console, and you can also view the individual test case reports.


## Error Handling
- Returns HTTP 500 for internal server errors
- Logs exceptions for debugging

## Performance Considerations
- Caches PDFs to avoid regeneration
- Uses efficient byte stream processing
