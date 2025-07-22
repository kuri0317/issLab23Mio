package unibo.spring;

import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public final SessionManager sessionManager = new SessionManager();
    //gestisce le sessioni con i Client connessi, trova le richieste e inoltra le risposte
    public final String clientPath = "serviceAGui";  

    public final ServiceAGUI serviceAGui = new ServiceAGUI(sessionManager);

    public WebSocketConfig() {
        sessionManager.setManager(serviceAGui);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sessionManager, "/ws") // stesso path del frontend
               .setAllowedOrigins("*");
    }
}
