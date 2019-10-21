package agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Utils;
import utils.contracts.LateScanAgentSubscription;
import utils.contracts.QueueSizeAnswerer;

import java.util.Vector;


public class LuggageAgent extends AbstractAgent {

    private int trailFreeSpace;
    private Vector<AID> peopleScanAgents;

    public LuggageAgent(){
        peopleScanAgents = new Vector<>();
        trailFreeSpace = Utils.MAX_LUGGAGE_CAPACITY;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Luggage-agent "+getAID().getName()+" is ready.");
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

        peopleScanAgents = Utils.findAvailableScanAgents(this);
        Utils.acceptNewScanAgents(this);

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        Utils.allocatePersonToBeScanned(this);
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }
}
