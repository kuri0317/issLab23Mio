package unibo.spring;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.tcp.TcpClientSupport;
import unibo.basicomm23.utils.CommUtils;
//
//import org.eclipse.californium.core.CoapHandler;
//import org.eclipse.californium.core.CoapResponse;
//import unibo.basicomm23.coap.CoapConnection;

public class Connector {
    private final ServiceAGUI serviceAccessGUI;
    private final Interaction tcp_connection;
    
    private final String sender;
    private final String destination;
    //private final CoapConnection coapConn;

    public Connector(ServiceAGUI serviceAccessGUI, String sender, String destination) {
        this.serviceAccessGUI = serviceAccessGUI;

        this.sender = sender; // Spring Server
        this.destination = destination; // coldstorage

        // TCP connection
        try {
            // ci colleghiamo al contesto del coldstorage
            tcp_connection = TcpClientSupport.connect("localhost", 8040, 10);
            System.out.println("TCP connection, session : " + tcp_connection);
            
        } catch (Exception e) {
            System.out.println("TCP connection error");
            throw new RuntimeException(e);

        }
        
    }
    
    private void sentAtCStorage(IApplMessage message, String requestId) {
        IApplMessage response = null;
        try {
            System.out.println("sentAtCStorage, msg sent: " + message);
            response = tcp_connection.request(message);
            System.out.println(" response arrived: " + response);
        } catch (Exception e) {
            System.out.println("sentAtCStorage error");
        }
        if (response != null) {
            System.out.println("response=" + response.msgContent());
            serviceAccessGUI.responseFromCStorage(response.msgContent(), requestId);
        }
    }

    protected void getCurrentWeight(String requestId) {
        IApplMessage message = CommUtils.buildRequest(
            sender, 
            "getCurrentWeight", 
            "getCurrentWeight(NO_PARAM)", 
            "coldroom"
        );
        sentAtCStorage(message, requestId);
        System.out.println("Sending weight request to coldroom: " + message);
    }
    
    protected IApplMessage depositRequest(String requestId) {
        IApplMessage message = CommUtils.buildRequest(
        		sender, 
        		"depositRequest", 
        		"depositRequest(NO_PARAM)", 
        		destination);
        
        sentAtCStorage(message, requestId);
        return message;
    }

    protected void checkMyTicket(String ticket, String requestId) {
        
        serviceAccessGUI.addPendingTicketCheck(requestId, ticket); // memorizzare i ticket 
        IApplMessage message = CommUtils.buildRequest(
        		sender, 
        		"checkmyticket", 
        		"checkmyticket(" + ticket + ")", 
        		destination);
        sentAtCStorage(message, requestId);
    
    }
} 