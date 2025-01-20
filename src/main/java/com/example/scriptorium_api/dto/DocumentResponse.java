package com.example.scriptorium_api.dto;

import com.example.scriptorium_api.model.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;
    private String name;
    private String title;
    private String content; // Nullable for folders
    private String type;    // "FILE" or "FOLDER"
    private Long parentId;  // Nullable for root-level documents

    /**
     * Static method to map a DocumentEntity to a DocumentResponse
     */
    public static DocumentResponse fromEntity(DocumentEntity documentEntity) {
        return DocumentResponse.builder()
                .id(documentEntity.getId())
                .name(documentEntity.getName())
                .title(documentEntity.getTitle())
                .content(documentEntity.getType() == DocumentEntity.Type.FILE ? documentEntity.getContent() : null)
                .type(documentEntity.getType().name())
                .parentId(documentEntity.getParentId())
                .build();
    }
}
