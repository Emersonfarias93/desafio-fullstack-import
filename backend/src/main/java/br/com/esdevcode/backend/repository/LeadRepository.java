package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    Optional<Lead> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT l
        FROM Lead l
        WHERE (:nome IS NULL OR LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:email IS NULL OR LOWER(l.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:origem IS NULL OR LOWER(l.origem) LIKE LOWER(CONCAT('%', :origem, '%')))
    """)
    Page<Lead> findByFilters(
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("origem") String origem,
            Pageable pageable
    );
}