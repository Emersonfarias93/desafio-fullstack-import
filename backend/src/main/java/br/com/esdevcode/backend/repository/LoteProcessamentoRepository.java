package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.LoteProcessamento;
import br.com.esdevcode.backend.model.enums.ProcessamentoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoteProcessamentoRepository extends JpaRepository<LoteProcessamento, UUID> {

    List<LoteProcessamento> findByLoteIdOrderByNumeroChunkAsc(UUID loteId);

    List<LoteProcessamento> findByLoteIdAndStatus(UUID loteId, ProcessamentoStatus status);

    long countByLoteId(UUID loteId);

    long countByLoteIdAndStatus(UUID loteId, ProcessamentoStatus status);
}
