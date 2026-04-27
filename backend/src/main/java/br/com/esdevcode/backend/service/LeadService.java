package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.model.entities.Lead;
import br.com.esdevcode.backend.repository.LeadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Transactional(readOnly = true)
    public Page<Lead> findByFilters(String nome, String email, String origem, Pageable pageable) {
        return leadRepository.findByFilters(
                normalizarFiltro(nome),
                normalizarFiltro(email),
                normalizarFiltro(origem),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Optional<Lead> findByEmail(String email) {
        return leadRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return leadRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Set<String> findExistingEmails(Collection<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return Set.of();
        }

        List<String> normalizados = emails.stream()
                .filter(email -> email != null && !email.isBlank())
                .map(email -> email.trim().toLowerCase())
                .distinct()
                .toList();

        Set<String> existentes = new HashSet<>();
        for (int inicio = 0; inicio < normalizados.size(); inicio += 1000) {
            int fim = Math.min(inicio + 1000, normalizados.size());
            existentes.addAll(leadRepository.findExistingEmails(normalizados.subList(inicio, fim)));
        }

        return existentes;
    }

    @Transactional
    public Lead save(Lead lead) {
        return leadRepository.save(lead);
    }

    @Transactional
    public boolean insertIgnore(String nome, String email, String telefone, String origem, LocalDateTime dataCadastro) {
        int linhas = leadRepository.insertIgnore(
                UUID.randomUUID(),
                nome,
                email == null ? null : email.trim().toLowerCase(),
                telefone,
                origem,
                dataCadastro
        );

        return linhas == 1;
    }

    private String normalizarFiltro(String filtro) {
        return filtro == null ? "" : filtro.trim();
    }
}
