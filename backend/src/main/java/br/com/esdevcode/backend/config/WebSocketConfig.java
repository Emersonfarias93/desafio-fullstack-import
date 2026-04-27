package br.com.esdevcode.backend.config;

import br.com.esdevcode.backend.websocket.LoteStatusWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LoteStatusWebSocketHandler loteStatusWebSocketHandler;

    public WebSocketConfig(LoteStatusWebSocketHandler loteStatusWebSocketHandler) {
        this.loteStatusWebSocketHandler = loteStatusWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(loteStatusWebSocketHandler, "/ws/lotes")
                .setAllowedOriginPatterns("*");
    }
}
