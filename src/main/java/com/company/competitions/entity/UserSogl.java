package com.company.competitions.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "USER_SOGL", indexes = {
        @Index(name = "IDX_USER_SOGL_USER", columnList = "USER_ID"),
        @Index(name = "IDX_USER_SOGL_SOGLASOVANIE", columnList = "SOGLASOVANIE_ID")
})
@Entity
public class UserSogl {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "COMMENT_")
    @Lob
    private String comment;

    @JoinColumn(name = "SOGLASOVANIE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Soglasovanie soglasovanie;

    public Soglasovanie getSoglasovanie() {
        return soglasovanie;
    }

    public void setSoglasovanie(Soglasovanie soglasovanie) {
        this.soglasovanie = soglasovanie;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}