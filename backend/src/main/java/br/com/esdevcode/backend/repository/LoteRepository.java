package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.enums.LoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {

    List<Lote> findByStatusOrderByCriadoEmDesc(LoteStatus status);

    Page<Lote> findAllByOrderByCriadoEmDesc(Pageable pageable);

    long countByStatus(LoteStatus status);

    @Query("SELECT COALESCE(SUM(l.totalLinhas), 0) FROM Lote l")
    long sumTotalLinhas();

    @Query("SELECT COALESCE(SUM(l.totalErros), 0) + COALESCE(SUM(l.totalInvalidas), 0) FROM Lote l")
    long sumTotalErros();
}
