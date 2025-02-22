package com.example.scriptorium_api.service;

import com.example.scriptorium_api.model.DocumentEntity;
import com.example.scriptorium_api.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<DocumentEntity> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<DocumentEntity> getChildrenByParentId(Long parentId) {
        if (parentId == null) {
            return documentRepository.findByParent(null);
        } else {
            return documentRepository.findById(parentId)
                    .map(documentRepository::findByParent)
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found with ID: " + parentId));
        }
    }

    public DocumentEntity saveDocument(DocumentEntity documentEntity) {
        DocumentEntity parent = documentEntity.getParent();
        if (parent != null && !documentRepository.existsById(parent.getId())) {
            throw new IllegalArgumentException("Parent document not found with ID: " + parent.getId());
        }

        if (documentRepository.existsByNameAndParent(documentEntity.getName(), parent)) {
            throw new IllegalArgumentException("A document with this name already exists under the specified parent.");
        }

        return documentRepository.save(documentEntity);
    }

    public void deleteDocumentById(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Document not found with ID: " + id);
        }
        documentRepository.deleteById(id);
    }

    public DocumentEntity moveDocument(Long documentId, Long newParentId) {
        DocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        DocumentEntity newParent = (newParentId != null)
                ? documentRepository.findById(newParentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found with ID: " + newParentId))
                : null;

        document.setParent(newParent);
        return documentRepository.save(document);
    }

    public DocumentEntity renameDocument(Long id, String newName) {
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (documentRepository.existsByNameAndParent(newName, document.getParent())) {
            throw new IllegalArgumentException("A document with this name already exists under the same parent.");
        }

        document.setName(newName);
        return documentRepository.save(document);
    }
}
