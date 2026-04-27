package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.exception.ResourceNotFoundException;
import br.com.esdevcode.backend.model.dto.LoteItemUpdateDTO;
import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import br.com.esdevcode.backend.repository.LoteItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class LoteItemService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter FORMATO_BR_COMPLETO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_BR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LoteItemRepository loteItemRepository;

    public LoteItemService(LoteItemRepository loteItemRepository) {
        this.loteItemRepository = loteItemRepository;
    }

    @Transactional
    public LoteItem save(LoteItem loteItem) {
        return loteItemRepository.save(loteItem);
    }

    @Transactional
    public List<LoteItem> saveAll(List<LoteItem> itens) {
        return loteItemRepository.saveAll(itens);
    }

    @Transactional
    public void saveAllEmLotes(List<LoteItem> itens, int tamanhoLote) {
        for (int inicio = 0; inicio < itens.size(); inicio += tamanhoLote) {
            int fim = Math.min(inicio + tamanhoLote, itens.size());
            loteItemRepository.saveAll(itens.subList(inicio, fim));
            loteItemRepository.flush();
        }
    }

    @Transactional
    public LoteItem atualizar(UUID loteId, UUID itemId, LoteItemUpdateDTO dto) {
        LoteItem item = loteItemRepository.findByIdAndLoteId(itemId, loteId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do lote nao encontrado: " + itemId));

        if (Boolean.TRUE.equals(dto.ignorar())) {
            item.setStatus(LoteItemStatus.IGNORADO);
            item.setDecisaoUsuario(DecisaoUsuario.IGNORAR);
            item.setMotivo("Ignorado pelo usuario.");
            return loteItemRepository.save(item);
        }

        item.setNome(limpar(dto.nome()));
        item.setEmail(limpar(dto.email()).toLowerCase());
        item.setTelefone(limpar(dto.telefone()));
        item.setOrigem(limpar(dto.origem()));
        item.setDataCadastro(parseData(dto.dataCadastro()));

        String erro = validarItem(item);
        if (erro == null) {
            item.setStatus(LoteItemStatus.VALIDO);
            item.setDecisaoUsuario(DecisaoUsuario.IMPORTAR);
            item.setMotivo(null);
        } else {
            item.setStatus(LoteItemStatus.INVALIDO);
            item.setDecisaoUsuario(DecisaoUsuario.PENDENTE);
            item.setMotivo(erro);
        }

        return loteItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Page<LoteItem> findByLoteId(UUID loteId, Pageable pageable) {
        return loteItemRepository.findByLoteId(loteId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<LoteItem> findByLoteIdAndStatus(UUID loteId, LoteItemStatus status, Pageable pageable) {
        return loteItemRepository.findByLoteIdAndStatus(loteId, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<LoteItem> findPendingDecisionByLoteId(UUID loteId) {
        return loteItemRepository.findByLoteIdAndDecisaoUsuario(loteId, DecisaoUsuario.PENDENTE);
    }

    @Transactional(readOnly = true)
    public long countByLoteId(UUID loteId) {
        return loteItemRepository.countByLoteId(loteId);
    }

    @Transactional(readOnly = true)
    public long countByLoteIdAndStatus(UUID loteId, LoteItemStatus status) {
        return loteItemRepository.countByLoteIdAndStatus(loteId, status);
    }

    private String validarItem(LoteItem item) {
        if (item.getNome() == null || item.getNome().isBlank()) {
            return "Nome obrigatorio";
        }
        if (item.getEmail() == null || item.getEmail().isBlank()) {
            return "Email obrigatorio";
        }
        if (!EMAIL_PATTERN.matcher(item.getEmail()).matches()) {
            return "Email invalido";
        }
        if (item.getDataCadastro() == null) {
            return "Data de cadastro invalida";
        }

        return null;
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private LocalDateTime parseData(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(valor, FORMATO_BR_COMPLETO);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(valor, FORMATO_BR_DATA).atStartOfDay();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(valor).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
