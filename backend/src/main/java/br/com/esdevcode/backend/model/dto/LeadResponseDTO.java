package br.com.esdevcode.backend.model.dto;

import br.com.esdevcode.backend.model.entities.Lead;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        String origem,
        LocalDateTime dataCadastro,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static LeadResponseDTO fromEntity(Lead lead) {
        return new LeadResponseDTO(
                lead.getId(),
                lead.getNome(),
                lead.getEmail(),
                lead.getTelefone(),
                lead.getOrigem(),
                lead.getDataCadastro(),
                lead.getCriadoEm(),
                lead.getAtualizadoEm()
        );
    }
}
