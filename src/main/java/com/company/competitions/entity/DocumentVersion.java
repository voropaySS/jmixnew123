package com.company.competitions.entity;

import io.jmix.core.FileRef;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "DOCUMENT_VERSION", indexes = {
        @Index(name = "IDX_DOCUMENT_VERSION_DOCUMENT", columnList = "DOCUMENT_ID")
})
@Entity
public class DocumentVersion {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Lob
    @Column(name = "FILE_REF")
    private FileRef fileRef;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "NUM_VERSION")
    private Integer numVersion;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @JoinColumn(name = "DOCUMENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Document document;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "docVersion")
    private DocRole docRole;

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public DocRole getDocRole() {
        return docRole;
    }

    public void setDocRole(DocRole docRole) {
        this.docRole = docRole;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Integer getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(Integer numVersion) {
        this.numVersion = numVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileRef getFileRef() {
        return fileRef;
    }

    public void setFileRef(FileRef fileRef) {
        this.fileRef = fileRef;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}