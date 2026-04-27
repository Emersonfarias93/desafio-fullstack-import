package br.com.esdevcode.backend.messaging.consumer;

import br.com.esdevcode.backend.config.KafkaTopicConfig;
import br.com.esdevcode.backend.messaging.event.LoteChunkConcluidoEvent;
import br.com.esdevcode.backend.messaging.event.LoteFinalizadoEvent;
import br.com.esdevcode.backend.messaging.event.LoteIniciadoEvent;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.service.LoteProcessingService;
import br.com.esdevcode.backend.service.LoteService;
import br.com.esdevcode.backend.service.LoteStatusNotifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class LoteEventConsumer {

    private final ObjectMapper objectMapper;
    private final LoteProcessingService loteProcessingService;
    private final LoteService loteService;
    private final LoteStatusNotifier loteStatusNotifier;

    public LoteEventConsumer(
            ObjectMapper objectMapper,
            LoteProcessingService loteProcessingService,
            LoteService loteService,
            LoteStatusNotifier loteStatusNotifier
    ) {
        this.objectMapper = objectMapper;
        this.loteProcessingService = loteProcessingService;
        this.loteService = loteService;
        this.loteStatusNotifier = loteStatusNotifier;
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_LOTE_INICIADO)
    public void consumirLoteIniciado(String payload) {
        LoteIniciadoEvent event = read(payload, LoteIniciadoEvent.class);
        Lote lote = loteService.recalcularTotais(event.loteId());
        loteStatusNotifier.notificar(lote);
        loteProcessingService.processarLote(event.loteId());
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_LOTE_CHUNK_CONCLUIDO)
    public void consumirChunkConcluido(String payload) {
        LoteChunkConcluidoEvent event = read(payload, LoteChunkConcluidoEvent.class);
        Lote lote = loteService.recalcularTotais(event.loteId());
        loteStatusNotifier.notificar(lote);
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_LOTE_FINALIZADO)
    public void consumirLoteFinalizado(String payload) {
        LoteFinalizadoEvent event = read(payload, LoteFinalizadoEvent.class);
        Lote lote = event.sucesso()
                ? loteService.finalizar(event.loteId())
                : loteService.finalizarComErro(event.loteId(), event.mensagemErro());
        loteStatusNotifier.notificar(lote);
    }

    private <T> T read(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Evento Kafka invalido.", exception);
        }
    }
}
