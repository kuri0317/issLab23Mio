document.addEventListener('DOMContentLoaded', function() {
    // Elementi UI
    const elements = {
        currentWeight: document.getElementById("currentWeight"),
        maxWeight: document.getElementById("maxWeight"),
        progressBar: document.getElementById("coldroomProgress"),
        ticketsContainer: document.getElementById("ticketsContainer"),
        ticketInput: document.getElementById("ticketInput"),
        requestBtn: document.getElementById("requestBtn"),
        checkBtn: document.getElementById("checkBtn"),
        checkAllBtn: document.getElementById("checkAllBtn"),
        validationResults: document.getElementById("validationResults"),
        messageArea: document.getElementById("messageArea")
    };

    // Stato applicazione
    let tickets = [];
    let socket = null;

    function resetUI() {
        // lista ticket
        tickets = [];
        if (elements.ticketsContainer) {
            elements.ticketsContainer.innerHTML = '';
        }
        
        // Resetta input
        if (elements.ticketInput) {
            elements.ticketInput.value = '';
        }
        
        // Resetta peso
        updateWeightDisplay(0);
        
        // Pulisci 
        if (elements.messageArea) {
            elements.messageArea.innerHTML = '';
        }
        
        if (elements.validationResults) {
            elements.validationResults.innerHTML = '';
        }
    }

    // Inizializza WebSocket
    function initWebSocket() {
        try {
            socket = new WebSocket("ws://" + window.location.host + "/ws");
            
            socket.onopen = function() {
                console.log("Connessione WebSocket stabilita");
            };
            
            resetUI(); 
           

            socket.onclose = function() {
                console.log("Connessione WebSocket chiusa");
            };
            
            socket.onmessage = handleMessage;
            
        } catch (error) {
            console.error("Errore inizializzazione WebSocket:", error);
        }
    }

    // messaggi di sistema
    function showSystemMessage(message, isError = false) {
        if (!elements.messageArea) return;
        
        const msgElement = document.createElement('div');
        msgElement.className = `system-message ${isError ? 'error' : 'info'}`;
        msgElement.textContent = message;
        elements.messageArea.appendChild(msgElement);
        
        setTimeout(() => {
            msgElement.classList.add('fade-out');
            setTimeout(() => msgElement.remove(), 300);
        }, 5000);
    }

    // Gestione messaggi - chiedi sempre il peso 
    function handleMessage(event) {
        console.log("Messaggio ricevuto:", event.data);
        
        if (event.data.startsWith("ticket/")) {
            const ticketId = event.data.split('/')[1];
            addTicket(ticketId);
            showSystemMessage(`Ticket ${ticketId} emesso con successo`);
          
        }
        else if (event.data.startsWith("ticketcheck/")) {
            const [ticketId, isValid] = event.data.split('/')[1].split('|');
            showValidationResult(ticketId, isValid === 'true');
           
        }
        else if (event.data.startsWith("weight/")) {
            updateWeightDisplay(event.data.split('/')[1]);
        }
        else if (event.data === "reject" || event.data.includes("reject")) {
            showSystemMessage("Richiesta rifiutata: frigorifero pieno", true);
           
        }
        
    }

    // Gestione ticket----------------------------------------------------------

     // Richiesta ticket
    function requestTicket() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.send("depositRequest/");
            
        }
    }
    function addTicket(ticketId) {
        tickets.push({ id: ticketId, status: 'pending' });
        renderTickets();
    }

    function renderTickets() {
        if (!elements.ticketsContainer) return;
        
        elements.ticketsContainer.innerHTML = tickets.map(ticket => `
            <div class="ticket-item" id="ticket-${ticket.id}">
                <span>Ticket: ${ticket.id}</span>
                <span class="status pending">Pending</span>
            </div>
        `).join('');
    }

    function removeTicket(ticketId) {
        tickets = tickets.filter(t => t.id !== ticketId);
        renderTickets();
    }

    // Validazione ticket
    function checkSingleTicket() {
        const ticketId = elements.ticketInput?.value.trim();
        if (!ticketId) return alert("Inserisci un ticket");
        
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.send(`checkmyticket/${ticketId}`);
            removeTicket(ticketId);
        }
    }

    function checkAllTickets() {
        if (tickets.length === 0) return alert("Nessun ticket da validare");
        
        if (socket && socket.readyState === WebSocket.OPEN) {
            tickets.forEach(ticket => {
                socket.send(`checkmyticket/${ticket.id}`);
                
            });
            tickets = [];
            renderTickets();
        }
    }
    //mostra risultati della validazione
    function showValidationResult(ticketId, isValid) {
        if (!elements.validationResults) return;
        
        const resultElement = document.createElement('div');
        resultElement.className = `validation-item ${isValid ? 'validation-valid' : 'validation-invalid'}`;
        resultElement.innerHTML = `
            <strong>Ticket ${ticketId}</strong>
            <span>${isValid ? 'VALIDO' : 'INVALIDO'}</span>
            <small>${new Date().toLocaleTimeString()}</small>
        `;
        elements.validationResults.prepend(resultElement);
        
        setTimeout(() => {
            resultElement.classList.add('fade-out');
            setTimeout(() => resultElement.remove(), 300);
        }, 5000);
    }

    //Gestione peso-----------------------------------------------------------
    
    //aggiornamento peso
    function updateWeightDisplay(weight) {
        if (!elements.currentWeight || !elements.progressBar) {
            console.error("Elementi per il peso non trovati");
            return;
        }

        const weightNum = parseFloat(weight) || 0;
        elements.currentWeight.textContent = weightNum.toFixed(1);
        //setTimeout(requestCurrentWeight, 500);
        updateProgressBar(weightNum);
    }

    function updateProgressBar(weight) {
        const percentage = Math.min(100, (weight / 60) * 100);
        elements.progressBar.style.width = `${percentage}%`;
        elements.progressBar.style.background = 
            percentage > 80 ? '#e74c3c' : 
            percentage > 50 ? '#f39c12' : '#2ecc71';
    }

    // Inizializzazione---------------------------------------------
    function init() {

        resetUI();
        
        // event listeners
        elements.requestBtn?.addEventListener('click', requestTicket);
        elements.checkBtn?.addEventListener('click', checkSingleTicket);
        elements.checkAllBtn?.addEventListener('click', checkAllTickets);

        // connessione WebSocket
        initWebSocket();
    }

    init();
});
