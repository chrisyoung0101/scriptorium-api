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
@ActiveProfiles("test") // Use the 'test' profile for tests
public class ScriptoriumApiApplicationTests {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Clean the database before each test
        documentRepository.deleteAll();
        System.out.println("Database cleaned. Record count: " + documentRepository.count());

        // Initialize MockMvc and ObjectMapper
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSaveAndFetchDocument() {
        String uniqueName = "UniqueName_" + System.currentTimeMillis();

        DocumentEntity document = DocumentEntity.builder()
                .name(uniqueName)
                .title("Test Title")
                .content("Test Content")
                .type(DocumentEntity.Type.FILE) // Explicitly set type
                .build();

        DocumentEntity savedDocument = documentRepository.save(document);

        assertNotNull(savedDocument.getId(), "The saved document's ID should not be null.");
        Optional<DocumentEntity> fetchedDocument = documentRepository.findById(savedDocument.getId());
        assertTrue(fetchedDocument.isPresent(), "The document should be present in the database.");
        assertEquals(uniqueName, fetchedDocument.get().getName(), "The name should match.");
        assertEquals("Test Title", fetchedDocument.get().getTitle(), "The title should match.");
        assertEquals("Test Content", fetchedDocument.get().getContent(), "The content should match.");
        assertEquals(DocumentEntity.Type.FILE, fetchedDocument.get().getType(), "The type should match.");
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

        DocumentEntity savedDocument = documentRepository.save(firstDocument);
        assertNotNull(savedDocument.getId(), "The first document's ID should not be null.");

        // Create and try to save the second document with the same name
        DocumentEntity secondDocument = DocumentEntity.builder()
                .name(uniqueBaseName) // Same name
                .title("Second Document")
                .content("Content of the second document")
                .type(DocumentEntity.Type.FILE) // Explicitly set type
                .build();

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> documentRepository.save(secondDocument),
                "Saving a document with a duplicate name should throw a DataIntegrityViolationException."
        );
    }

    @Test
    public void testSaveFolderAndFetchChildren() {
        String parentFolderName = "ParentFolder_" + System.currentTimeMillis();

        DocumentEntity parentFolder = DocumentEntity.builder()
                .name(parentFolderName)
                .title("Parent Folder")
                .type(DocumentEntity.Type.FOLDER) // Create as FOLDER
                .build();

        DocumentEntity savedParentFolder = documentRepository.save(parentFolder);
        assertNotNull(savedParentFolder.getId(), "The parent folder's ID should not be null.");

        DocumentEntity childFile = DocumentEntity.builder()
                .name("ChildFile_" + System.currentTimeMillis())
                .title("Child File")
                .content("This is a child file.")
                .parent(savedParentFolder) // Set the parent folder
                .type(DocumentEntity.Type.FILE) // Set as FILE
                .build();

        DocumentEntity savedChildFile = documentRepository.save(childFile);
        assertNotNull(savedChildFile.getId(), "The child file's ID should not be null.");
        assertEquals(savedParentFolder.getId(), savedChildFile.getParent().getId(), "The parent folder should match.");
    }

    @Test
    public void testControllerCreateAndFetchDocument() throws Exception {
        // Create a new document via the controller
        DocumentEntity requestDocument = DocumentEntity.builder()
                .name("ControllerDoc_" + System.currentTimeMillis())
                .title("Controller Test Title")
                .content("Controller Test Content")
                .type(DocumentEntity.Type.FILE)
                .build();

        String requestJson = objectMapper.writeValueAsString(requestDocument);

        // Perform POST request to create the document
        String responseJson = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the response and validate
        DocumentResponse response = objectMapper.readValue(responseJson, DocumentResponse.class);
        assertNotNull(response.getId(), "The response ID should not be null.");
        assertEquals(requestDocument.getName(), response.getName(), "The names should match.");
        assertEquals(requestDocument.getTitle(), response.getTitle(), "The titles should match.");
    }

    @Test
    public void testControllerGetRootDocuments() throws Exception {
        // Create a root-level document
        DocumentEntity rootDocument = DocumentEntity.builder()
                .name("RootDoc_" + System.currentTimeMillis())
                .title("Root Document Title")
                .type(DocumentEntity.Type.FOLDER)
                .build();

        documentRepository.save(rootDocument);

        // Perform GET request to fetch root-level documents
        mockMvc.perform(get("/api/documents/children")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(rootDocument.getName()))
                .andExpect(jsonPath("$[0].type").value(rootDocument.getType().name()));
    }
}
