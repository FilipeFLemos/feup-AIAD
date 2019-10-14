package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Vector;

public class QueueManagerAgent extends Agent {

    private Vector<AID> luggageAgents;
    private Vector<AID> peopleScanAgents;

    public QueueManagerAgent(){
        luggageAgents = new Vector<>();
        peopleScanAgents = new Vector<>();
    }

    @Override
    protected void setup() {
        findAgents();
    }

    private void findAgents() {
        DFAgentDescription template = getDFAgentDescriptionTemplate("luggage");
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found " + result.length + " Luggage Control Agents.");
            for (DFAgentDescription agentDescription : result) {
                luggageAgents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        template = getDFAgentDescriptionTemplate("peopleScan");
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found " + result.length + " People Scan Agents.");
            for (DFAgentDescription agentDescription : result) {
                peopleScanAgents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private DFAgentDescription getDFAgentDescriptionTemplate(String type) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        return template;
    }
}
