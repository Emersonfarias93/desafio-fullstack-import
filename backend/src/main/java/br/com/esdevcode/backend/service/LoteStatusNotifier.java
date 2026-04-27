package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.model.dto.LoteResponseDTO;
import br.com.esdevcode.backend.model.entities.Lote;
import br.com.esdevcode.backend.websocket.LoteStatusWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoteStatusNotifier {

    private final LoteStatusWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public LoteStatusNotifier(LoteStatusWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    public void notificar(Lote lote) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "type", "lote.status",
                    "lote", LoteResponseDTO.fromEntity(lote)
            ));
            webSocketHandler.broadcast(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar status do lote.", exception);
        }
    }
}
