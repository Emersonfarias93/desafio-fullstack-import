package br.com.esdevcode.backend.model.dto;

import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.model.enums.LoteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteResponseDTO(
        UUID id,
        String nomeArquivo,
        LoteStatus status,
        Integer totalLinhas,
        Integer totalValidas,
        Integer totalInvalidas,
        Integer totalNovas,
        Integer totalDuplicadas,
        Integer totalPossiveisDuplicadas,
        Integer totalImportadas,
        Integer totalIgnoradas,
        Integer totalErros,
        Integer totalProcessadas,
        Integer progressoPercentual,
        String mensagemErro,
        LocalDateTime criadoEm,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm
) {
    public static LoteResponseDTO fromEntity(Lote lote) {
        return new LoteResponseDTO(
                lote.getId(),
                lote.getNomeArquivo(),
                lote.getStatus(),
                lote.getTotalLinhas(),
                lote.getTotalValidas(),
                lote.getTotalInvalidas(),
                lote.getTotalNovas(),
                lote.getTotalDuplicadas(),
                lote.getTotalPossiveisDuplicadas(),
                lote.getTotalImportadas(),
                lote.getTotalIgnoradas(),
                lote.getTotalErros(),
                calcularTotalProcessadas(lote),
                calcularProgresso(lote),
                lote.getMensagemErro(),
                lote.getCriadoEm(),
                lote.getIniciadoEm(),
                lote.getFinalizadoEm()
        );
    }

    private static Integer calcularTotalProcessadas(Lote lote) {
        return lote.getTotalImportadas()
                + lote.getTotalIgnoradas()
                + lote.getTotalDuplicadas()
                + lote.getTotalInvalidas()
                + lote.getTotalErros();
    }

    private static Integer calcularProgresso(Lote lote) {
        if (lote.getTotalLinhas() == null || lote.getTotalLinhas() == 0) {
            return lote.getStatus() == LoteStatus.FINALIZADO ? 100 : 0;
        }

        int processadas = calcularTotalProcessadas(lote);
        int progresso = (int) Math.round((processadas * 100.0) / lote.getTotalLinhas());
        return Math.max(0, Math.min(100, progresso));
    }
}
