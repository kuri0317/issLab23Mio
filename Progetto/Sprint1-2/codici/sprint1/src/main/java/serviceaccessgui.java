
package main.java;
 
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import java.util.Scanner;		

public class serviceaccessgui {	
      
    //protected IApplMessage goonMsg = CommUtils.buildDispatch("user","goon","goon(ok)",  "mapbuilder");

    protected Interaction conn;					//mantiene connessione TCP
    protected String currentTicket = "";		//memoria del ticket
    protected final Scanner scanner = new Scanner(System.in);		//legge input da tastiera

    
      public void doJob() {
        String hostAddr       = "localhost";
        int port              = 8040;					//indico la porta della connessione, uguale a quella del contesto qak
        ProtocolType protocol = ProtocolType.tcp;		//indico tipo di protocollo

        //Interaction conn = ConnectionFactory.createClientSupport(protocol, hostAddr, ""+port);
        this.conn = ConnectionFactory.createClientSupport(protocol, hostAddr, ""+port);			//crea la connessione
        
        
        try {
        	 {
        	        while (true) {		//ciclo while per indicare le richieste di ticket e verifica
        	            System.out.println("\n=== Menu Cold Storage ===");
        	            System.out.println("1 - Richiedi ticket deposito");
        	            System.out.println("2 - Verifica validità ticket");
        	            System.out.println("0 - Esci");
        	            System.out.print("Scelta: ");
        	            
        	            String choice = scanner.nextLine();
        	            
        	            switch (choice) {
        	                case "1":
        	                    requestTicket();
        	                    break;
        	                case "2":
        	                    checkTicket();
        	                    break;
        	                case "0":
        	                    return;
        	                default:
        	                    System.out.println("Scelta non valida");
        	            }
        	        }
        	    }
        	 
        }catch (Exception e) {
                  CommUtils.outred("callertcp ERROR:" + e.getMessage() );
            }
      }
      
      public void requestTicket() {		//richiesta del ticket
          try {
              // Invia richiesta ticket -struttura della richiesta
              IApplMessage request = CommUtils.buildRequest(
                  "servicegui", 
                  "depositRequest", 
                  "depositRequest(NO_PARAM)", 
                  "coldstorageservice"
              );
              
              System.out.println("Invio richiesta ticket...");
              IApplMessage reply = conn.request(request);
              
              //Gestione risposta
              if(reply.msgId().equals("accept")) {
                  currentTicket = reply.msgContent();
                  System.out.println("Ticket accettato: " + currentTicket);
              } else {
                  System.out.println("Richiesta rifiutata: deposito pieno");
              }
              
          } catch (Exception e) {	//eccezione
              System.err.println("Errore: " + e.getMessage());
          }
      }
      //controlla il ticket
      public void checkTicket() {
          if(currentTicket.isEmpty()) {
              System.out.println("Nessun ticket disponibile");
              return;
          }
          
          try {
              // Invia richiesta di verifica - struttura richiesta
              IApplMessage request = CommUtils.buildRequest(
                  "servicegui", 
                  "checkmyticket", 
                  "checkmyticket(" + currentTicket + ")", 
                  "coldstorageservice"
              );
              
              System.out.println("Verifico ticket: " + currentTicket);
              IApplMessage reply = conn.request(request);
              
              // gestiamo risposta
              String content = reply.msgContent(); // "ticketcheck(true)"
              boolean isValid = content.contains("(true)"); // Cerca "(true)"

              System.out.println("Ticket valido? " + (isValid ? "SI" : "NO"));
              
          } catch (Exception e) {
              System.err.println("Errore: " + e.getMessage());
          }
      }
      
      //main da cui si crea la classe e attiva il job
      public static void main(String[] args) {
    	  serviceaccessgui appl = new  serviceaccessgui();
            appl.doJob();
      }
      
}
