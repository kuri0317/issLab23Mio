### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('provautArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxservicearea', graph_attr=nodeattr):
          coldstorageservice=Custom('coldstorageservice','./qakicons/symActorWithobjSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorWithobjSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorWithobjSmall.png')
          serviceaccesgui=Custom('serviceaccesgui','./qakicons/symActorWithobjSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     coldstorageservice >> Edge(color='magenta', style='solid', decorate='true', label='<takecharge<font color="darkgreen"> chargetaken</font> &nbsp; quit &nbsp; >',  fontcolor='magenta') >> transporttrolley
     coldroom >> Edge(color='magenta', style='solid', decorate='true', label='<frigofull &nbsp; >',  fontcolor='magenta') >> coldstorageservice
     transporttrolley >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     coldstorageservice >> Edge(color='magenta', style='solid', decorate='true', label='<delete_Reservation &nbsp; fwrequest<font color="darkgreen"> fwYES fwNO</font> &nbsp; load_CR &nbsp; >',  fontcolor='magenta') >> coldroom
     coldstorageservice >> Edge(color='blue', style='solid',  decorate='true', label='<goHome &nbsp; >',  fontcolor='blue') >> transporttrolley
     transporttrolley >> Edge(color='blue', style='solid',  decorate='true', label='<setdirection &nbsp; disengage &nbsp; >',  fontcolor='blue') >> basicrobot
     transporttrolley >> Edge(color='blue', style='solid',  decorate='true', label='<liberoTT &nbsp; >',  fontcolor='blue') >> coldstorageservice
     coldstorageservice >> Edge(color='blue', style='solid',  decorate='true', label='<truckDriver &nbsp; >',  fontcolor='blue') >> serviceaccesgui
diag
