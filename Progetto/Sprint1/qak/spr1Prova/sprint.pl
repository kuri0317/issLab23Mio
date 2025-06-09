%====================================================================================
% sprint description   
%====================================================================================
request( depositRequest, depositRequest(TICKET) ).
reply( accept, accept(TICKET) ).  %%for depositRequest
reply( reject, reject(NO_PARAM) ).  %%for depositRequest
request( checkmyticket, checkmyticket(TICKET) ).
reply( ticketcheck, ticketcheck(BOOL) ).  %%for checkmyticket
dispatch( truckDriver, truckDriver(NO_PARAM) ).
request( fwrequest, fwrequest(NO_PARAM) ).
reply( fwYES, fwYES(NO_PARAM) ).  %%for fwrequest
reply( fwNO, fwNO(NO_PARAM) ).  %%for fwrequest
request( load_CR, load_CR(NO_PARAM) ).
request( delete_Reservation, delete_Reservation(NO_PARAM) ).
request( takecharge, takecharge(NO_PARAM) ).
reply( chargetaken, chargetaken(NO_PARAM) ).  %%for takecharge
dispatch( liberoTT, liberoTT(NO_PARAM) ).
dispatch( goHome, goHome(NO_PARAM) ).
%====================================================================================
context(ctxservicearea, "localhost",  "TCP", "8040").
 qactor( coldstorageservice, ctxservicearea, "it.unibo.coldstorageservice.Coldstorageservice").
 static(coldstorageservice).
  qactor( coldroom, ctxservicearea, "it.unibo.coldroom.Coldroom").
 static(coldroom).
  qactor( transporttrolley, ctxservicearea, "it.unibo.transporttrolley.Transporttrolley").
 static(transporttrolley).
  qactor( serviceaccessgui, ctxservicearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
 static(serviceaccessgui).
