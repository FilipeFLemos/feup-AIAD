package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import utils.Utils;

public class LuggageAgent extends Agent {

    private int trailFreeSpace;
    private AID queueManager = null;

    public LuggageAgent(){
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

        addBehaviour(new TrailSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

    //Private class responsible for answering queue manager about the available size on its trail
    class TrailSizeAnswerer extends ContractNetResponder {

        TrailSizeAnswerer(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) {
            LuggageAgent luggageAgent = (LuggageAgent) myAgent;
            if (luggageAgent.queueManager == null){
                luggageAgent.queueManager = cfp.getSender();
            }

            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent("I have space for " + trailFreeSpace + " luggage!");

            return reply;
        }

        @Override
        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            //System.out.println(myAgent.getLocalName() + " got a reject...");
        }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            System.out.println(myAgent.getLocalName() + " was selected to pick the next luggage!");

            ACLMessage reply = accept.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Will be done");
            //TODO: update trailfreespace

            return reply;
        }

    }

}
