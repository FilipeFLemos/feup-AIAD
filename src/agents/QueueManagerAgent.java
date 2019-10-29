package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import utils.Utils;
import utils.contracts.QueueSizeQuery;

import java.util.Vector;

public class QueueManagerAgent extends Agent {

    private Vector<AID> luggageAgents;
    private Vector<AID> peopleScanAgents;

    public QueueManagerAgent() {
        luggageAgents = new Vector<>();
        peopleScanAgents = new Vector<>();
    }

    @Override
    protected void setup() {
        findAvailableAgents();
        acceptNewAgents();
    }

    private void findAvailableAgents() {
        luggageAgents = Utils.findAvailableAgents(this, "luggage");
        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
    }

    private void acceptNewAgents() {
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "luggage"));
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));
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
