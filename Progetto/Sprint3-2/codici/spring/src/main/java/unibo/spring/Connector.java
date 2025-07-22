package unibo.spring;

//SPRINT3 PT2

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.tcp.TcpClientSupport;
import unibo.basicomm23.utils.CommUtils;
//
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.basicomm23.coap.CoapConnection;

public class Connector {
    private final ServiceAGUI serviceAccessGUI;
    private final Interaction tcp_connection;
    
    private final String sender;
    private final String destination;
    
    private CoapConnection coapConn;

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
        
     // CoAP Observation (per coldroom)
        
        try {
            this.coapConn = new CoapConnection("127.0.0.1:8040", "ctxservicearea/coldroom");
            System.out.println("CoAP observer setup on port 8040 for ctxservicearea/coldroom");
            
            // Configura per aggiornamenti del peso
            setupCoapObserver();
        } catch (Exception e) {
            System.err.println("CoAP connection failed: " + e.getMessage());
        }
    }
    
    private void setupCoapObserver() {
        try {
            coapConn.observeResource(new CoapHandler() {
                @Override
                public void onLoad(CoapResponse response) {
                    if(response != null && response.isSuccess()) {
                        String payload = response.getResponseText().trim();
                        System.out.println("CoAP Update received - Raw payload: " + payload);
                        
                        try {
                            float weight = Float.parseFloat(payload);
                            serviceAccessGUI.updateColdRoomState(weight, 60.0f); // MAX_WEIGHT = 60
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing weight value: " + payload);
                        }
                    }
                }

                @Override
                public void onError() {
                    System.err.println("CoAP observation error");
                }
            });
        } catch (Exception e) {
            System.err.println("Observer setup failed: " + e.getMessage());
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

    protected void sendWeightUpdate(int peso) {
    	String msg = "weightUpdate(" + peso + ")";
        IApplMessage weightMsg = CommUtils.buildDispatch(
                "coldroom", "updateWeight", "updateWeight(" + peso + ")", "handler");
        
        try {
            tcp_connection.forward(weightMsg);
        } catch (Exception e) {
            System.err.println("Error sending weight update: " + e.getMessage());
        }
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