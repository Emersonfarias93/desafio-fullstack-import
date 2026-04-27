package br.com.esdevcode.backend.repository;

import br.com.esdevcode.backend.model.entities.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    Optional<Lead> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT LOWER(l.email)
        FROM Lead l
        WHERE LOWER(l.email) IN :emails
    """)
    List<String> findExistingEmails(@Param("emails") Collection<String> emails);

    @Modifying
    @Query(value = """
            INSERT INTO leads(id, nome, email, telefone, origem, data_cadastro, criado_em)
            VALUES (:id, :nome, :email, :telefone, :origem, :dataCadastro, CURRENT_TIMESTAMP)
            ON CONFLICT (email) DO NOTHING
            """, nativeQuery = true)
    int insertIgnore(
            @Param("id") UUID id,
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("telefone") String telefone,
            @Param("origem") String origem,
            @Param("dataCadastro") LocalDateTime dataCadastro
    );

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
