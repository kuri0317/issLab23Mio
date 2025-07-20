package unibo.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class SessionManager extends AbstractWebSocketHandler {
	
	//liste per tenere traccia delle sessioni
    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final Map<String, WebSocketSession> pendingRequests = new HashMap<>();
    private ServiceAGUI serviceAGui;


    protected void setManager(ServiceAGUI serviceAGui) {
        this.serviceAGui = serviceAGui;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println(" client connected to the session: " + session);

        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        
    	sessions.remove(session);
        pendingRequests.remove(session);
        System.out.println("client disconnected to the session, removing: " + session);

        super.afterConnectionClosed(session, status);
    }

    // Messages sent from client
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    	
        String msg = message.getPayload();
        System.out.println(" msg received: " + msg);

        this.serviceAGui.requestFromClient(msg, newRequest(session));
    }

    protected void sendToAll(String message) {
        System.out.println("message to all clients: " + message);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                System.out.println("Error in sendToAll");
            }
        }
    }

    protected void sendToClient(String message, String requestId) {
        System.out.println("message to client: " + message);
        WebSocketSession session = this.pendingRequests.get(requestId);
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            System.out.println("Error in sendToClient, msg: " + message + ", to session: " + session);
        }
    }

    //new session request
    protected String newRequest(WebSocketSession session) {
        String requestId = "req" + session.getId().substring(session.getId().lastIndexOf('-') + 1);  //cosa significa questa cosa? 
        System.out.println("newRequest: " + requestId);
        pendingRequests.put(requestId, session);
        return requestId;
    }
}