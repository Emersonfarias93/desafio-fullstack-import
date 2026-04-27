package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.model.entities.LoteItem;
import br.com.esdevcode.backend.model.entities.LoteProcessamento;
import br.com.esdevcode.backend.model.enums.DecisaoUsuario;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import br.com.esdevcode.backend.repository.LoteItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CsvChunkProcessingService {

    private final LoteProcessamentoService loteProcessamentoService;
    private final LoteItemRepository loteItemRepository;
    private final LeadService leadService;

    public CsvChunkProcessingService(
            LoteProcessamentoService loteProcessamentoService,
            LoteItemRepository loteItemRepository,
            LeadService leadService
    ) {
        this.loteProcessamentoService = loteProcessamentoService;
        this.loteItemRepository = loteItemRepository;
        this.leadService = leadService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ChunkProcessingResult processar(UUID processamentoId, int numeroChunk, List<UUID> itemIds) {
        Instant inicio = Instant.now();
        LoteProcessamento processamento = loteProcessamentoService.iniciar(processamentoId);
        UUID loteId = processamento.getLote().getId();

        int importados = 0;
        int duplicados = 0;
        int erros = 0;
        List<LoteItem> atualizados = new ArrayList<>();

        for (LoteItem item : loteItemRepository.findAllById(itemIds)) {
            if (item.getStatus() != LoteItemStatus.VALIDO && item.getStatus() != LoteItemStatus.APROVADO) {
                continue;
            }

            try {
                boolean inserido = leadService.insertIgnore(
                        item.getNome(),
                        item.getEmail(),
                        item.getTelefone(),
                        item.getOrigem(),
                        item.getDataCadastro()
                );

                if (inserido) {
                    item.setStatus(LoteItemStatus.IMPORTADO);
                    item.setDecisaoUsuario(DecisaoUsuario.IMPORTAR);
                    importados++;
                } else {
                    item.setStatus(LoteItemStatus.DUPLICADO_EXATO);
                    item.setDecisaoUsuario(DecisaoUsuario.IGNORAR);
                    item.setMotivo("Email ja importado por outro processamento.");
                    duplicados++;
                }
            } catch (RuntimeException exception) {
                item.setStatus(LoteItemStatus.ERRO);
                item.setMotivo(exception.getMessage());
                erros++;
            }

            atualizados.add(item);
        }

        loteItemRepository.saveAll(atualizados);
        long tempoMs = Duration.between(inicio, Instant.now()).toMillis();
        loteProcessamentoService.concluir(processamentoId, importados, erros, tempoMs);

        return new ChunkProcessingResult(
                loteId,
                processamentoId,
                numeroChunk,
                itemIds.size(),
                importados,
                duplicados,
                erros,
                tempoMs
        );
    }
}
