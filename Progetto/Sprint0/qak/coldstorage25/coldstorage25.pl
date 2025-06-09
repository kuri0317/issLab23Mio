%====================================================================================
% coldstorage25 description   
%====================================================================================
request( depositRequest, depositRequest(NO_PARAM) ).
request( checkmyticket, checkmyticket(TICKET) ).
%====================================================================================
context(ctxservicearea, "localhost",  "TCP", "8080").
 qactor( coldroom, ctxservicearea, "it.unibo.coldroom.Coldroom").
 static(coldroom).
  qactor( transporttrolley, ctxservicearea, "it.unibo.transporttrolley.Transporttrolley").
 static(transporttrolley).
  qactor( coldstorageservice, ctxservicearea, "it.unibo.coldstorageservice.Coldstorageservice").
 static(coldstorageservice).
  qactor( serviceaccessgui, ctxservicearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
 static(serviceaccessgui).
