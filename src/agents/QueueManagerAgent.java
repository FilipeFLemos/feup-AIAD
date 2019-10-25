package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.Utils;
import utils.contracts.LateSubscription;
import utils.contracts.QueueSizeQuery;

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
        findAvailableAgents();
        acceptNewAgents();
    }

    private void findAvailableAgents() {
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("luggage");
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println(this.getLocalName() + ": Found " + result.length + " Luggage Control Agents.");
            for (DFAgentDescription agentDescription : result) {
                luggageAgents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        //peopleScanAgents = Utils.findAvailableScanAgents(this);
        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
    }

    private void acceptNewAgents() {
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("luggage");
        addBehaviour(new LateSubscription(this, template, "luggage"));
        template = Utils.getDFAgentDescriptionTemplate("scan");
        addBehaviour(new LateSubscription(this, template, "scan"));
    }

    public void allocateLuggage() {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("How many luggage can you receive?");
        addBehaviour(new QueueSizeQuery(this, msg, "luggage"));
    }

    public Vector<AID> getLuggageAgents() {
        return luggageAgents;
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }
}
