package com.invoiceGen.dao;

import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Repository
public class PdfStorageRepository {
    private static final String PDF_STORAGE_DIR = "generated-pdfs/";

    public PdfStorageRepository() {
        // Ensure directory exists
        try {
            Files.createDirectories(Paths.get(PDF_STORAGE_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create PDF storage directory", e);
        }
    }

    public String storePdf(byte[] pdfContent) throws IOException {
        // Generate unique filename
        String filename = UUID.randomUUID().toString() + ".pdf";
        Path filePath = Paths.get(PDF_STORAGE_DIR + filename);
        
        // Write PDF to file
        Files.write(filePath, pdfContent);
        
        return filename;
    }

    public byte[] retrievePdf(String filename) throws IOException {
        Path filePath = Paths.get(PDF_STORAGE_DIR + filename);
        return Files.readAllBytes(filePath);
    }

    public boolean pdfExists(String filename) {
        File file = new File(PDF_STORAGE_DIR + filename);
        return file.exists();
    }

    public String generateUniqueHash(Object data) {
        // Create a simple hash based on JSON representation of the data
        return Base64.getEncoder().encodeToString(data.toString().getBytes());
    }
}