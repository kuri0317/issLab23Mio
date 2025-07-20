%====================================================================================
% led description   
%====================================================================================
dispatch( ledOff, ledOff(NO_PARAM) ).
dispatch( inmoto, inmoto(NO_PARAM) ).
dispatch( ledOn, ledOn(NO_PARAM) ).
%====================================================================================
context(ctxled, "localhost",  "TCP", "8044").
context(ctxservicearea, "127.0.0.1",  "TCP", "8040").
 qactor( led, ctxled, "it.unibo.led.Led").
 static(led).
  qactor( transporttrolley, ctxservicearea, "external").
