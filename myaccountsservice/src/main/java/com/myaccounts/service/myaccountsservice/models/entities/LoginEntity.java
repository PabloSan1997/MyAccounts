package com.myaccounts.service.myaccountsservice.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "login")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String jwt;

    private Instant created;

    private Instant updated;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;

    @PrePersist
    public void prePersist(){
        this.created = Instant.now();
        this.updated = Instant.now();
        this.active = true;
    }

    @PreUpdate
    public void preUpdate(){
        this.updated = Instant.now();
    }
}