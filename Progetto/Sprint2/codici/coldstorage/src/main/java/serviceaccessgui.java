
package main.java;
 
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;		

public class serviceaccessgui {	
      
    //protected IApplMessage goonMsg = CommUtils.buildDispatch("user","goon","goon(ok)",  "mapbuilder");

    protected Interaction conn;					//mantiene connessione TCP
    protected List<String> ticketList = new ArrayList<>(); // memoria tickets
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
    	            System.out.println("2 - Verifica ultimo ticket");
                    System.out.println("3 - Verifica ticket specifico");
                    System.out.println("4 - Mostra ticket attivi");
                    System.out.println("0 - Esci");
                    System.out.print("Scelta: ");
    	            
    	            String choice = scanner.nextLine();
    	            
    	            switch (choice) {
    	                case "1":
    	                    requestTicket();
    	                    break;
    	                case "2":
                            if (!ticketList.isEmpty()) {
                                checkTicket(ticketList.get(ticketList.size()-1));
                            } else {
                                System.out.println("Nessun ticket disponibile");
                            }
                            break;
    	                case "3":
                            validateSpecificTicket();
                            break;
                        case "4":
                            showActiveTickets();
                            break;
    	                case "0":
    	                	sendQuitCommand();
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
            	  String newTicket = reply.msgContent();
                  ticketList.add(newTicket);
                  System.out.println("Ticket accettato: " + newTicket);
                  System.out.println("Ticket attivi: " + ticketList.size());
                  
              } else {
                  System.out.println("Richiesta rifiutata");
              }
              
          } catch (Exception e) {	//eccezione
              System.err.println("Errore: " + e.getMessage());
          }
      }
      //controlla il ticket
      public void validateSpecificTicket() {
          if(ticketList.isEmpty()) {
              System.out.println("Nessun ticket disponibile");
              return;
          }
          
          showActiveTickets();
          
          System.out.print("Scegli il numero del ticket da validare (0 per annullare): ");
          try {
              int choice = Integer.parseInt(scanner.nextLine());
              
              if(choice > 0 && choice <= ticketList.size()) {
                  checkTicket(ticketList.get(choice-1));
              } else if(choice != 0) {
                  System.out.println("Scelta non valida");
              }
          } catch (NumberFormatException e) {
              System.out.println("Inserisci un numero valido");
          }
      }
      private void checkTicket(String ticket) {
          try {
              IApplMessage request = CommUtils.buildRequest(
                  "servicegui", 
                  "checkmyticket", 
                  "checkmyticket(" + ticket + ")", 
                  "coldstorageservice"
              );
              
              System.out.println("Verifico ticket: " + ticket);
              IApplMessage reply = conn.request(request);
              
              boolean isValid = Boolean.parseBoolean(reply.msgContent());
              System.out.println("Ticket valido? " + (isValid ? "SI" : "NO"));
              
              if(isValid) {//cancellare ticket usati
                  ticketList.remove(ticket);
                  System.out.println("Ticket rimosso dalla lista");
              }else if(!isValid) {
            	  ticketList.remove(ticket);
                  System.out.println("Ticket rimosso dalla lista");
              }
          } catch (Exception e) {
              System.err.println("Errore: " + e.getMessage());
          }
      }
      

      private void showActiveTickets() {
          if(ticketList.isEmpty()) {
              System.out.println("Nessun ticket disponibile");
              return;
          }
          
          System.out.println("\nTicket disponibili:");
          for(int i = 0; i < ticketList.size(); i++) {
              System.out.println((i+1) + " - " + ticketList.get(i));
          }
      }
      

		private void sendQuitCommand() {
		    try {
		        IApplMessage quitMsg = CommUtils.buildRequest(
		            "servicegui",
		            "quit",
		            "quit(NO_PARAM)",
		            "coldstorageservice"
		        );
		        conn.request(quitMsg);
		        System.out.println("Sistema in chiusura...");
		    } catch (Exception e) {
		        System.err.println("Errore durante la chiusura: " + e.getMessage());
		    }
		}
		     
//      public static void doBeeps() {
//            java.awt.Toolkit.getDefaultToolkit().beep();
//            CommUtils.delay(1500);
//            java.awt.Toolkit.getDefaultToolkit().beep();
//      }
//      public static void doBeep() {
//            java.awt.Toolkit.getDefaultToolkit().beep();
//      }
      
      //main da cui si crea la classe e attiva il job
      public static void main(String[] args) {
    	  serviceaccessgui appl = new  serviceaccessgui();
            appl.doJob();
      }
      
}