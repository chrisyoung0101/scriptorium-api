package com.example.scriptorium_api.controller;

import com.example.scriptorium_api.dto.DocumentResponse;
import com.example.scriptorium_api.model.DocumentEntity;
import com.example.scriptorium_api.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    @Operation(
            summary = "Create a new document or folder",
            description = """
                Creates a new file or folder in the system. Folders can act as parent nodes to other documents,
                while files are leaf nodes. The `type` must be specified as either `FILE` or `FOLDER`.
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document or folder created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public DocumentResponse createDocument(
            @RequestBody @Parameter(description = "The document or folder details to create.") DocumentEntity documentEntity) {
        DocumentEntity savedEntity = documentService.saveDocument(documentEntity);
        return DocumentResponse.fromEntity(savedEntity);
    }

    @GetMapping
    @Operation(
            summary = "Get all documents and folders",
            description = "Fetches a flat list of all documents and folders in the system, regardless of hierarchy.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all documents and folders retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public List<DocumentResponse> getAllDocuments() {
        return documentService.getAllDocuments()
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a document or folder by ID",
            description = "Retrieves a specific document or folder using its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document or folder retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public DocumentResponse getDocumentById(
            @PathVariable @Parameter(description = "The unique ID of the document or folder to retrieve.") Long id) {
        DocumentEntity entity = documentService.getDocumentById(id);
        return DocumentResponse.fromEntity(entity);
    }

    @GetMapping("/{parentId}/children")
    @Operation(
            summary = "Get child documents and folders by parent ID",
            description = """
                Fetches all immediate children (both files and folders) under a specific parent folder. 
                If the parent is a folder, its contents are returned.
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of child documents and folders retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public List<DocumentResponse> getChildrenByParentId(
            @PathVariable @Parameter(description = "The unique ID of the parent folder.") Long parentId) {
        return documentService.getChildrenByParentId(parentId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @GetMapping("/children")
    @Operation(
            summary = "Get all root-level documents and folders",
            description = """
                Fetches all documents and folders that are not nested under any parent (root-level). 
                Useful for building the top-level of a file hierarchy.
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of root-level documents and folders retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public List<DocumentResponse> getChildren() {
        return documentService.getChildrenByParentId(null)
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @PutMapping("/{id}/rename")
    @Operation(
            summary = "Rename a document or folder",
            description = """
                Renames a specific document or folder by its unique ID. The new name must be unique 
                at the same hierarchy level (e.g., no duplicate names in the same folder).
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document or folder renamed successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public DocumentResponse renameDocument(
            @PathVariable @Parameter(description = "The unique ID of the document or folder to rename.") Long id,
            @RequestBody @Parameter(description = "The new name for the document or folder.") String newName) {
        DocumentEntity updatedEntity = documentService.renameDocument(id, newName);
        return DocumentResponse.fromEntity(updatedEntity);
    }

    @PutMapping("/{id}/move")
    @Operation(
            summary = "Move a document or folder to a new parent",
            description = """
                Moves a document or folder to a new parent folder. Specify the ID of the target folder as the `parentId` 
                in the request body. If `parentId` is null, the document is moved to the root level.
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document or folder moved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.class)))
            }
    )
    public DocumentResponse moveDocument(
            @PathVariable @Parameter(description = "The unique ID of the document or folder to move.") Long id,
            @RequestBody @Parameter(description = "The ID of the new parent folder. Pass null for root-level.") Long newParentId) {
        DocumentEntity movedEntity = documentService.moveDocument(id, newParentId);
        return DocumentResponse.fromEntity(movedEntity);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a document or folder by ID",
            description = """
                Deletes a document or folder from the database using its unique ID. If the document is a folder, 
                all its children and their subchildren will also be deleted recursively. This action is irreversible.
                """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Document or folder deleted successfully."),
                    @ApiResponse(responseCode = "404", description = "Document or folder not found.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid request.",
                            content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<Void> deleteDocumentById(
            @PathVariable @Parameter(description = "The unique ID of the document or folder to delete.") Long id) {
        documentService.deleteDocumentById(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }



}
