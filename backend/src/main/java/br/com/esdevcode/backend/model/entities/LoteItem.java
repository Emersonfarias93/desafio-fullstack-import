package br.com.esdevcode.backend.model.entities;

import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "lote_itens",
        indexes = {
                @Index(name = "idx_lote_itens_lote_id", columnList = "lote_id"),
                @Index(name = "idx_lote_itens_status", columnList = "status"),
                @Index(name = "idx_lote_itens_email", columnList = "email"),
                @Index(name = "idx_lote_itens_nome", columnList = "nome")
        }
)
public class LoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private Integer linhaCsv;

    @Column(length = 255)
    private String nome;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(length = 100)
    private String origem;

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoteItemStatus status;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_existente_id")
    private Lead leadExistente;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private DecisaoUsuario decisaoUsuario;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();

        if (this.status == null) {
            this.status = LoteItemStatus.NOVO;
        }

        if (this.decisaoUsuario == null) {
            this.decisaoUsuario = DecisaoUsuario.PENDENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
