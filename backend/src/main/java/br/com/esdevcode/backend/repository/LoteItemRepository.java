package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoteItemRepository extends JpaRepository<LoteItem, UUID> {

    Page<LoteItem> findByLoteId(UUID loteId, Pageable pageable);

    Page<LoteItem> findByLoteIdAndStatus(UUID loteId, LoteItemStatus status, Pageable pageable);

    List<LoteItem> findByLoteIdAndDecisaoUsuario(UUID loteId, DecisaoUsuario decisaoUsuario);

    long countByLoteId(UUID loteId);

    long countByLoteIdAndStatus(UUID loteId, LoteItemStatus status);
}
