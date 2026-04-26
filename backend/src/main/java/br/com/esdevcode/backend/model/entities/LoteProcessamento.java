package br.com.esdevcode.backend.model.entities;

import br.com.esdevcode.backend.model.enums.ProcessamentoStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "lote_processamentos",
        indexes = {
                @Index(name = "idx_lote_processamentos_lote_id", columnList = "lote_id")
        }
)
public class LoteProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private Integer numeroChunk;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProcessamentoStatus status;

    @Column(nullable = false)
    private Integer totalLinhas = 0;

    @Column(nullable = false)
    private Integer totalSucesso = 0;

    @Column(nullable = false)
    private Integer totalErros = 0;

    @Column(nullable = false)
    private Long tempoMs = 0L;

    @Column(columnDefinition = "TEXT")
    private String mensagemErro;

    private LocalDateTime iniciadoEm;

    private LocalDateTime finalizadoEm;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ProcessamentoStatus.PENDENTE;
        }
    }
}