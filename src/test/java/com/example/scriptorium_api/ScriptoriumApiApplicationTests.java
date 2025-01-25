package com.example.scriptorium_api;

import com.example.scriptorium_api.dto.DocumentResponse;
import com.example.scriptorium_api.model.DocumentEntity;
import com.example.scriptorium_api.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test") // Explicitly use 'test' profile
public class ScriptoriumApiApplicationTests {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        documentRepository.deleteAll(); // Clean the database before each test
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSaveAndFetchDocument() {
        DocumentEntity document = DocumentEntity.builder()
                .name("TestFile")
                .title("Test Title")
                .content("Test Content")
                .type(DocumentEntity.Type.FILE)
                .build();

        DocumentEntity savedDocument = documentRepository.save(document);

        assertNotNull(savedDocument.getId());
        Optional<DocumentEntity> fetchedDocument = documentRepository.findById(savedDocument.getId());
        assertTrue(fetchedDocument.isPresent());
        assertEquals("TestFile", fetchedDocument.get().getName());
    }

    @Test
    public void testDuplicateNameThrowsException() {
        String uniqueBaseName = "DuplicateName_" + System.currentTimeMillis();

        // Create and save the first document
        DocumentEntity firstDocument = DocumentEntity.builder()
                .name(uniqueBaseName)
                .title("First Document")
                .content("Content of the first document")
                .type(DocumentEntity.Type.FILE) // Explicitly set type
                .build();

        documentRepository.save(firstDocument); // Save the first document

        // Create and attempt to save the second document with the same name
        DocumentEntity secondDocument = DocumentEntity.builder()
                .name(uniqueBaseName) // Same name
                .title("Second Document")
                .content("Content of the second document")
                .type(DocumentEntity.Type.FILE) // Explicitly set type
                .build();

        // Assert that a DataIntegrityViolationException is thrown
        Exception exception = assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> documentRepository.saveAndFlush(secondDocument), // Ensure flush is called to trigger the constraint
                "Saving a document with a duplicate name should throw a DataIntegrityViolationException."
        );

        assertNotNull(exception, "Expected exception should not be null.");
    }


    @Test
    public void testControllerCreateAndFetchDocument() throws Exception {
        DocumentEntity document = DocumentEntity.builder()
                .name("ControllerDoc")
                .title("Controller Title")
                .content("Controller Content")
                .type(DocumentEntity.Type.FILE)
                .build();

        String json = objectMapper.writeValueAsString(document);

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ControllerDoc"))
                .andExpect(jsonPath("$.title").value("Controller Title"));
    }
}
