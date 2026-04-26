package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.enums.LoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {

    List<Lote> findByStatusOrderByCriadoEmDesc(LoteStatus status);
}