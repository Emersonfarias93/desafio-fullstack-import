package br.com.esdevcode.backend.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "leads",
        indexes = {
                @Index(name = "idx_leads_nome", columnList = "nome"),
                @Index(name = "idx_leads_origem", columnList = "origem")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_leads_email", columnNames = "email")
        }
)
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(length = 100)
    private String origem;

    private LocalDate dataCadastro;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}