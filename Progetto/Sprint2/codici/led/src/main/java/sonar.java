
package main.java;
 
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;		

public class sonar {	
      
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
    	        while (true) {		//ciclo while per richieste
    	            System.out.println("\n=== Menu Cold Storage ===");
    	            System.out.println("1 - Robot incontra ostacolo");
    	            System.out.println("2 - Ostacolo rimosso");
                    System.out.println("0 - Esci");
                    System.out.print("Scelta: ");
    	            
    	            String choice = scanner.nextLine();
    	            
    	            switch (choice) {
    	                case "1":
    	                    stop();
    	                    break;
    	                case "2":
                            resume();
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
      
      public void stop() {		//richiesta del ticket
    	  try {
    	        IApplMessage stopMsg = CommUtils.buildEvent(  // 
    	            "sonar",
    	            "stop",
    	            "stop(NO_PARAM)"
    	        );
    	        conn.forward(stopMsg);  
    	        System.out.println("Invio stop...");
    	        System.out.println("C'è un ostacolo...");
    	    }catch (Exception e) {
		        System.err.println("Errore durante la chiusura: " + e.getMessage());
		    }
      }
      //controlla il ticket
      public void resume() {
          
    	  try {
    	        IApplMessage resumeMsg = CommUtils.buildDispatch(  // Dispatch
    	            "sonar",
    	            "resume",
    	            "resume(NO_PARAM)",
    	            "transporttrolley"
    	        );
    	        conn.forward(resumeMsg);  
    	        System.out.println("Invio resume...");
    	        System.out.println("L'ostacolo è stato rimosso...");
    	    }  catch (Exception e) {
		        System.err.println("Errore durante la chiusura: " + e.getMessage());
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
      
      //main da cui si crea la classe e attiva il job
      public static void main(String[] args) {
    	  sonar appl = new  sonar();
            appl.doJob();
      }
      
}