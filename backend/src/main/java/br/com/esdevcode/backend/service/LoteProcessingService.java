package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.config.ImportProperties;
import br.com.esdevcode.backend.messaging.event.LoteChunkConcluidoEvent;
import br.com.esdevcode.backend.messaging.producer.LoteEventProducer;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.entities.LoteProcessamento;
import br.com.esdevcode.backend.model.enums.LoteItemStatus;
import br.com.esdevcode.backend.repository.LoteItemRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LoteProcessingService {

    private final LoteService loteService;
    private final LoteProcessamentoService loteProcessamentoService;
    private final LoteItemRepository loteItemRepository;
    private final CsvChunkProcessingService csvChunkProcessingService;
    private final LoteEventProducer loteEventProducer;
    private final ImportProperties importProperties;
    private final Executor csvChunkExecutor;

    public LoteProcessingService(
            LoteService loteService,
            LoteProcessamentoService loteProcessamentoService,
            LoteItemRepository loteItemRepository,
            CsvChunkProcessingService csvChunkProcessingService,
            LoteEventProducer loteEventProducer,
            ImportProperties importProperties,
            @Qualifier("csvChunkExecutor") Executor csvChunkExecutor
    ) {
        this.loteService = loteService;
        this.loteProcessamentoService = loteProcessamentoService;
        this.loteItemRepository = loteItemRepository;
        this.csvChunkProcessingService = csvChunkProcessingService;
        this.loteEventProducer = loteEventProducer;
        this.importProperties = importProperties;
        this.csvChunkExecutor = csvChunkExecutor;
    }

    public void processarLote(UUID loteId) {
        Lote lote = loteService.buscarPorId(loteId);
        List<UUID> itemIds = loteItemRepository.findIdsByLoteIdAndStatusInOrderByLinhaCsv(
                loteId,
                EnumSet.of(LoteItemStatus.VALIDO, LoteItemStatus.APROVADO)
        );

        if (itemIds.isEmpty()) {
            loteEventProducer.publicarLoteFinalizado(loteId, true, null);
            return;
        }

        List<List<UUID>> chunks = particionar(itemIds, importProperties.chunkSize());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean houveErro = new AtomicBoolean(false);

        for (int i = 0; i < chunks.size(); i++) {
            int numeroChunk = i + 1;
            List<UUID> chunk = chunks.get(i);
            LoteProcessamento processamento = loteProcessamentoService.criarProcessamento(lote, numeroChunk, chunk.size());

            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(
                            () -> csvChunkProcessingService.processar(processamento.getId(), numeroChunk, chunk),
                            csvChunkExecutor
                    )
                    .thenAccept(resultado -> loteEventProducer.publicarChunkConcluido(new LoteChunkConcluidoEvent(
                            resultado.loteId(),
                            resultado.processamentoId(),
                            resultado.numeroChunk(),
                            resultado.totalLinhas(),
                            resultado.totalImportados(),
                            resultado.totalDuplicados(),
                            resultado.totalErros(),
                            resultado.tempoMs(),
                            LocalDateTime.now()
                    )))
                    .exceptionally(exception -> {
                        houveErro.set(true);
                        loteProcessamentoService.finalizarComErro(
                                processamento.getId(),
                                exception.getMessage(),
                                0L
                        );
                        return null;
                    });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        if (houveErro.get()) {
            loteEventProducer.publicarLoteFinalizado(loteId, false, "Um ou mais chunks falharam.");
        } else {
            loteEventProducer.publicarLoteFinalizado(loteId, true, null);
        }
    }

    private List<List<UUID>> particionar(List<UUID> ids, int tamanhoChunk) {
        List<List<UUID>> chunks = new ArrayList<>();

        for (int inicio = 0; inicio < ids.size(); inicio += tamanhoChunk) {
            int fim = Math.min(inicio + tamanhoChunk, ids.size());
            chunks.add(List.copyOf(ids.subList(inicio, fim)));
        }

        return chunks;
    }
}
