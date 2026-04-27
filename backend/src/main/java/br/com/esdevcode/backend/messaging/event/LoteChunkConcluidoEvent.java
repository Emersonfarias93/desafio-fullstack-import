package br.com.esdevcode.backend.messaging.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteChunkConcluidoEvent(
        UUID loteId,
        UUID processamentoId,
        int numeroChunk,
        int totalLinhas,
        int totalImportados,
        int totalDuplicados,
        int totalErros,
        long tempoMs,
        LocalDateTime ocorridoEm
) {
}
