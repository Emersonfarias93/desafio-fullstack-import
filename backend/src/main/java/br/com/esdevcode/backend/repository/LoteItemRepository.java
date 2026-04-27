package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteItemRepository extends JpaRepository<LoteItem, UUID> {

    Page<LoteItem> findByLoteId(UUID loteId, Pageable pageable);

    Page<LoteItem> findByLoteIdAndStatus(UUID loteId, LoteItemStatus status, Pageable pageable);

    Optional<LoteItem> findByIdAndLoteId(UUID id, UUID loteId);

    List<LoteItem> findByLoteIdAndDecisaoUsuario(UUID loteId, DecisaoUsuario decisaoUsuario);

    long countByLoteId(UUID loteId);

    long countByLoteIdAndStatus(UUID loteId, LoteItemStatus status);

    long countByLoteIdAndStatusIn(UUID loteId, Collection<LoteItemStatus> statuses);

    @Query("""
        SELECT i.id
        FROM LoteItem i
        WHERE i.lote.id = :loteId
          AND i.status IN :statuses
        ORDER BY i.linhaCsv ASC
    """)
    List<UUID> findIdsByLoteIdAndStatusInOrderByLinhaCsv(
            @Param("loteId") UUID loteId,
            @Param("statuses") Collection<LoteItemStatus> statuses
    );
}
