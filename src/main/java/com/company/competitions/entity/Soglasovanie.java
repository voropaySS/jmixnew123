package com.company.competitions.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "SOGLASOVANIE", indexes = {
        @Index(name = "IDX_SOGLASOVANIE_DOC_VERSION", columnList = "DOC_VERSION_ID")
})
@Entity
public class Soglasovanie {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OneToMany(mappedBy = "soglasovanie")
    private List<UserSogl> usersSogl;

    @Column(name = "STATUS_SOGL")
    private String statusSogl;

    @Column(name = "HISTORY")
    @Lob
    private String history;

    @JoinColumn(name = "DOC_VERSION_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentVersion docVersion;

    public DocumentVersion getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(DocumentVersion docVersion) {
        this.docVersion = docVersion;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getStatusSogl() {
        return statusSogl;
    }

    public void setStatusSogl(String statusSogl) {
        this.statusSogl = statusSogl;
    }

    public List<UserSogl> getUsersSogl() {
        return usersSogl;
    }

    public void setUsersSogl(List<UserSogl> usersSogl) {
        this.usersSogl = usersSogl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}