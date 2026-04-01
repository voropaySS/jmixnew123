package com.company.competitions.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "DOC_ROLE", indexes = {
        @Index(name = "IDX_DOC_ROLE_DOC_VERSION", columnList = "DOC_VERSION_ID"),
        @Index(name = "IDX_DOC_ROLE_OWNER", columnList = "OWNER_ID")
})
@Entity
public class DocRole {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "DOC_VERSION_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentVersion docVersion;

    @JoinColumn(name = "OWNER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private User owner;

    @JoinTable(name = "DOC_ROLE_USER_LINK",
            joinColumns = @JoinColumn(name = "DOC_ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    @ManyToMany
    private List<User> redaktori;

    public List<User> getRedaktori() {
        return redaktori;
    }

    public void setRedaktori(List<User> redaktori) {
        this.redaktori = redaktori;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public DocumentVersion getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(DocumentVersion docVersion) {
        this.docVersion = docVersion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}