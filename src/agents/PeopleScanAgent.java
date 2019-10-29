package agents;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.contracts.QueueSizeAnswerer;

public class PeopleScanAgent extends AbstractAgent {

    @Override
    protected void setup() {
        System.out.println("Hallo! PeopleScan-agent " + getAID().getName() + " is ready.");
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("scan");
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }
}
