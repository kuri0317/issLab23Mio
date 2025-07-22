package unibo.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceAGUI {

	private final Map<String, String> pendingTicketChecks = new ConcurrentHashMap<>(); //associare un requestId al ticketId durante la validazione
	private final Map<String, Boolean> ticketMap = new ConcurrentHashMap<>(); //mappa per tracciare lo stato dei ticket 
	//attivare connessioni
	private final SessionManager SessionManager;
    private final Connector serviceConnector;

    private Float current_weight = 0f;
    private static final float MAX_WEIGHT = 60f;

    public ServiceAGUI(SessionManager SessionManager) {
        this.SessionManager = SessionManager;
        this.SessionManager.setManager(this);
        this.serviceConnector = new Connector(this, "springServer", "coldstorageservice");
    }
    
    public void updateColdRoomState(float current, float max) {
        this.current_weight = current;
        
        String updateMsg = "weight/" + current;  //
        this.SessionManager.sendToAll(updateMsg);
        System.out.println("Weight updated via CoAP: " + current);
    }

//    
//    public void updateColdRoomWeight(float currentWeight) {
//        this.current_weight = currentWeight;
//        String updateMsg = "weight/" + currentWeight;
//        this.SessionManager.sendToAll(updateMsg);
//        
//        // Per debug
//        System.out.println("Updated weight to: " + currentWeight);
//    }
    
    public void responseFromCStorage(String msg, String requestId) {
        
        if (msg.contains("accept")) {
            String ticket = takeBool(msg);
            ticketMap.put(ticket, true);
            SessionManager.sendToClient("ticket/" + ticket, requestId);
            //this.serviceConnector.getCurrentWeight(""); //chiedi informazione sul peso
        } 
        else if (msg.equals("true") || msg.equals("false")) {
            // Risposta CStorage per ticketcheck validation
            String ticketId = extractTicketIdFromOriginalRequest(requestId);
            String response = "ticketcheck/" + ticketId + "|" + msg;
            System.out.println("Transformed response: " + response); 
            SessionManager.sendToClient(response, requestId);
            pendingTicketChecks.remove(requestId);
        }
        else if (msg.contains("reject")) {
            SessionManager.sendToClient("ticket_rejected", requestId);
        }
//        else if (msg.contains("currentWeight(")) {
//            String pesoStr = msg.replace("currentWeight(", "").replace(")", "");
//            float peso = Float.parseFloat(pesoStr);
//        }
    
    }
    
    private String extractTicketIdFromOriginalRequest(String requestId) {
        return pendingTicketChecks.getOrDefault(requestId, "UNKNOWN_TICKET");
    }
    public void addPendingTicketCheck(String requestId, String ticket) {
        pendingTicketChecks.put(requestId, ticket);
    }
    
    private String takeBool(String msg) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(msg);
        return matcher.find() ? matcher.group(1) : "";
    }
    
    public void requestFromClient(String msg, String requestId) {
        
        String[] parts = msg.split("/");
        
        switch (parts[0]) {
            case "depositRequest": 
                this.serviceConnector.depositRequest(requestId);
                break;
            case "checkmyticket": 
                String ticket = parts.length > 1 ? parts[1] : "";
                this.serviceConnector.checkMyTicket(ticket, requestId);
                break;
//	        case "getCurrentWeight":
//	            this.serviceConnector.getCurrentWeight(requestId);
//	            break;
            default:
                System.out.println("Unknown request type: " + parts[0]);
        }
    }
    
}