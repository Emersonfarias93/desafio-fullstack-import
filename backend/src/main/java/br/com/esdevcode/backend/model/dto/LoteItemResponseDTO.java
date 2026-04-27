package br.com.esdevcode.backend.model.dto;

import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteItemResponseDTO(
        UUID id,
        UUID loteId,
        Integer linhaCsv,
        String nome,
        String email,
        String telefone,
        String origem,
        LocalDateTime dataCadastro,
        LoteItemStatus status,
        String motivo,
        UUID leadExistenteId,
        DecisaoUsuario decisaoUsuario,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static LoteItemResponseDTO fromEntity(LoteItem item) {
        return new LoteItemResponseDTO(
                item.getId(),
                item.getLote() != null ? item.getLote().getId() : null,
                item.getLinhaCsv(),
                item.getNome(),
                item.getEmail(),
                item.getTelefone(),
                item.getOrigem(),
                item.getDataCadastro(),
                item.getStatus(),
                item.getMotivo(),
                item.getLeadExistente() != null ? item.getLeadExistente().getId() : null,
                item.getDecisaoUsuario(),
                item.getCriadoEm(),
                item.getAtualizadoEm()
        );
    }
}
