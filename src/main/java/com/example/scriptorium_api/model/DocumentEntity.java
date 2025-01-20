package com.example.scriptorium_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Add Lombok's @Builder for the builder pattern
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;


    @Enumerated(EnumType.STRING) // Map the enumeration to a database-friendly string
    @Column(nullable = false)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private DocumentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentEntity> children = new ArrayList<>();


    public Long getParentId() {
        return (parent != null) ? parent.getId() : null;
    }

    public enum Type {
        FOLDER,
        FILE
    }
}
