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

        findAvailableScanAgents();
        acceptNewScanAgents();

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

    private void findAvailableScanAgents(){
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("scan");
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

    private void acceptNewScanAgents() {
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("scan");
        addBehaviour(new LateScanAgentSubscription(this, template));
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }
}
