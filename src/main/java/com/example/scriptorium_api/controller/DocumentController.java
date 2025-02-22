package com.example.scriptorium_api.controller;

import com.example.scriptorium_api.model.DocumentEntity;
import com.example.scriptorium_api.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    // ✅ GET all documents
    @GetMapping
    public List<DocumentEntity> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    // ✅ POST to create a new document
    @PostMapping
    public ResponseEntity<DocumentEntity> createDocument(@RequestBody DocumentEntity documentEntity) {
        try {
            DocumentEntity savedDocument = documentService.saveDocument(documentEntity);
            return new ResponseEntity<>(savedDocument, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Server Error
        }
    }
}
