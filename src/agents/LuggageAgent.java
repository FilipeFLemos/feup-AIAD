package agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;
import utils.contracts.ClosestInspectorQuery;
import utils.contracts.LateScanAgentSubscription;
import utils.contracts.QueueSizeAnswerer;

import java.util.Random;
import java.util.Vector;

public class LuggageAgent extends AbstractAgent {

    private int trailFreeSpace;
    private Vector<AID> peopleScanAgents;
    private Vector<AID> inspectorAgents;
    private boolean hasIrregularLuggage;

    public LuggageAgent() {
        peopleScanAgents = new Vector<>();
        trailFreeSpace = Utils.MAX_LUGGAGE_CAPACITY;
        hasIrregularLuggage = randomizeHasIrregularLuggage();
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Luggage-agent " + getAID().getName() + " is ready.");
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("luggage");
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        /**
         * peopleScanAgents = Utils.findAvailableScanAgents(this);
         * Utils.acceptNewScanAgents(this);
         * 
         * inspectorAgents = Utils.findAvailableInspectorAgents(this);
         * Utils.acceptNewInspectorAgents(this);
         **/

        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        Utils.acceptNewAgents(this, "scan");
        inspectorAgents = Utils.findAvailableAgents(this, "inspector");
        Utils.acceptNewAgents(this, "inspector");

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        Utils.allocatePersonToBeScanned(this);

        // addBehaviour(new ClosestInspectorAnswerer(this,
        // MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        // Utils.allocateClosestInspector(this);
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

    public Vector<AID> getInspectorAgents() {
        return inspectorAgents;
    }

    public boolean getHasIrregularLuggage() {
        return hasIrregularLuggage;
    }

    public boolean randomizeHasIrregularLuggage() {
        Random rand = new Random();
        if (rand.nextInt(101) > 90) {
            return true;
        }
        return false;
    }

    public void setHasIrregularLuggage(boolean isIrregular) {
        hasIrregularLuggage = isIrregular;
    }
}
