%====================================================================================
% provaut description   
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
request( frigofull, frigofull(NO_PARAM) ).
request( takecharge, takecharge(NO_PARAM) ).
reply( chargetaken, chargetaken(NO_PARAM) ).  %%for takecharge
dispatch( liberoTT, liberoTT(NO_PARAM) ).
dispatch( goHome, goHome(NO_PARAM) ).
request( quit, quit(NO_PARAM) ).
request( engage, engage(OWNER,STEPTIME) ).
reply( engagedone, engagedone(ARG) ).  %%for engage
reply( engagerefused, engagerefused(ARG) ).  %%for engage
dispatch( disengage, disengage(ARG) ).
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
request( doplan, doplan(PATH,OWNER,STEPTIME) ).
reply( doplandone, doplandone(ARG) ).  %%for doplan
reply( doplanfailed, doplanfailed(ARG) ).  %%for doplan
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
dispatch( setrobotstate, setpos(X,Y,D) ).
dispatch( setdirection, dir(D) ).
request( getrobotstate, getrobotstate(ARG) ).
reply( robotstate, robotstate(POS,DIR) ).  %%for getrobotstate
%====================================================================================
context(ctxservicearea, "localhost",  "TCP", "8040").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( coldstorageservice, ctxservicearea, "it.unibo.coldstorageservice.Coldstorageservice").
 static(coldstorageservice).
  qactor( coldroom, ctxservicearea, "it.unibo.coldroom.Coldroom").
 static(coldroom).
  qactor( transporttrolley, ctxservicearea, "it.unibo.transporttrolley.Transporttrolley").
 static(transporttrolley).
  qactor( serviceaccessgui, ctxservicearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
 static(serviceaccessgui).
  qactor( basicrobot, ctxbasicrobot, "external").
