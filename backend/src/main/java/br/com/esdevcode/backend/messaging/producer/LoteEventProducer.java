package br.com.esdevcode.backend.messaging.producer;

import br.com.esdevcode.backend.config.KafkaTopicConfig;
import br.com.esdevcode.backend.messaging.event.LoteChunkConcluidoEvent;
import br.com.esdevcode.backend.messaging.event.LoteFinalizadoEvent;
import br.com.esdevcode.backend.messaging.event.LoteIniciadoEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LoteEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public LoteEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicarLoteIniciado(UUID loteId) {
        LoteIniciadoEvent event = new LoteIniciadoEvent(loteId, LocalDateTime.now());
        enviar(KafkaTopicConfig.TOPIC_LOTE_INICIADO, loteId, event);
    }

    public void publicarChunkConcluido(LoteChunkConcluidoEvent event) {
        enviar(KafkaTopicConfig.TOPIC_LOTE_CHUNK_CONCLUIDO, event.loteId(), event);
    }

    public void publicarLoteFinalizado(UUID loteId, boolean sucesso, String mensagemErro) {
        LoteFinalizadoEvent event = new LoteFinalizadoEvent(loteId, sucesso, mensagemErro, LocalDateTime.now());
        enviar(KafkaTopicConfig.TOPIC_LOTE_FINALIZADO, loteId, event);
    }

    private void enviar(String topic, UUID loteId, Object event) {
        try {
            kafkaTemplate.send(topic, loteId.toString(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar evento Kafka.", exception);
        }
    }
}
