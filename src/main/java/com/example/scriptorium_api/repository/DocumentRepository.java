package com.example.scriptorium_api.repository;

import com.example.scriptorium_api.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    /**
     * Fetch all child documents and folders under a specific parent.
     *
     * @param parent the parent document entity.
     * @return a list of child documents and folders.
     */
    List<DocumentEntity> findByParent(DocumentEntity parent);

    /**
     * Fetch all root-level documents and folders (those without a parent).
     *
     * @return a list of root-level documents and folders.
     */
    List<DocumentEntity> findByParentIsNull();

    /**
     * Check if a document exists with a given name under a specific parent.
     *
     * @param name   the name of the document.
     * @param parent the parent document entity (null for root level).
     * @return true if a document with the given name exists; otherwise false.
     */
    boolean existsByNameAndParent(String name, DocumentEntity parent);
}
