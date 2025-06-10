%====================================================================================
% prova description   
%====================================================================================
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
context(ctxservicearea, "localhost",  "TCP", "8090").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( transporttrolley, ctxservicearea, "it.unibo.transporttrolley.Transporttrolley").
 static(transporttrolley).
  qactor( basicrobot, ctxbasicrobot, "external").
