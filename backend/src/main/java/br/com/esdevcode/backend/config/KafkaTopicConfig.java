package br.com.esdevcode.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    public static final String TOPIC_LOTE_INICIADO = "lote.iniciado";
    public static final String TOPIC_LOTE_CHUNK_CONCLUIDO = "lote.chunk.concluido";
    public static final String TOPIC_LOTE_FINALIZADO = "lote.finalizado";

    @Bean
    public NewTopic loteIniciadoTopic() {
        return TopicBuilder.name(TOPIC_LOTE_INICIADO).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic loteChunkConcluidoTopic() {
        return TopicBuilder.name(TOPIC_LOTE_CHUNK_CONCLUIDO).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic loteFinalizadoTopic() {
        return TopicBuilder.name(TOPIC_LOTE_FINALIZADO).partitions(3).replicas(1).build();
    }
}
