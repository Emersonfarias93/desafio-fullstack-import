package br.com.esdevcode.backend.model.entities;

import br.com.esdevcode.backend.model.enums.LoteStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nomeArquivo;

    @Column(nullable = false, length = 500)
    private String caminhoArquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoteStatus status;

    @Column(nullable = false)
    private Integer totalLinhas = 0;

    @Column(nullable = false)
    private Integer totalValidas = 0;

    @Column(nullable = false)
    private Integer totalInvalidas = 0;

    @Column(nullable = false)
    private Integer totalNovas = 0;

    @Column(nullable = false)
    private Integer totalDuplicadas = 0;

    @Column(nullable = false)
    private Integer totalPossiveisDuplicadas = 0;

    @Column(nullable = false)
    private Integer totalImportadas = 0;

    @Column(nullable = false)
    private Integer totalIgnoradas = 0;

    @Column(nullable = false)
    private Integer totalErros = 0;

    @Column(columnDefinition = "TEXT")
    private String mensagemErro;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime iniciadoEm;

    private LocalDateTime finalizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();

        if (this.status == null) {
            this.status = LoteStatus.RECEBIDO;
        }
    }
}
