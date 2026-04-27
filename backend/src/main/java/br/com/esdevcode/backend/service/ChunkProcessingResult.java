package br.com.esdevcode.backend.service;

import java.util.UUID;

public record ChunkProcessingResult(
        UUID loteId,
        UUID processamentoId,
        int numeroChunk,
        int totalLinhas,
        int totalImportados,
        int totalDuplicados,
        int totalErros,
        long tempoMs
) {
}
