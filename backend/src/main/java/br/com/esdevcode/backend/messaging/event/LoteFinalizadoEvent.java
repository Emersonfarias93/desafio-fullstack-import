package br.com.esdevcode.backend.messaging.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteFinalizadoEvent(
        UUID loteId,
        boolean sucesso,
        String mensagemErro,
        LocalDateTime ocorridoEm
) {
}
