package br.com.esdevcode.backend.messaging.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteIniciadoEvent(
        UUID loteId,
        LocalDateTime ocorridoEm
) {
}
