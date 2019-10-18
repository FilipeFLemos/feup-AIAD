package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.proto.SubscriptionInitiator;
import utils.Utils;
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
        findAgents();
        lateAgentSubscription();
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

        template = getDFAgentDescriptionTemplate("scan");
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

    private void lateAgentSubscription() {
        DFAgentDescription template = getDFAgentDescriptionTemplate("luggage");
        addBehaviour(new LuggageAgentSubscription(this, template));
    }

    public void allocateLuggage() {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("How many luggage can you receive?");
        addBehaviour(new QueueSizeQuery(this, msg, "luggage"));
    }

    public void allocatePerson() {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("What is your queue size?");
        addBehaviour(new QueueSizeQuery(this, msg, "scan"));
    }

    public Vector<AID> getLuggageAgents() {
        return luggageAgents;
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

    //Private class responsible for being alert to late new agents
    private class LuggageAgentSubscription extends SubscriptionInitiator {

        LuggageAgentSubscription(Agent agent, DFAgentDescription dfad) {
            super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                for (DFAgentDescription dfd : dfds) {
                    AID agent = dfd.getName();
                    if (!luggageAgents.contains(agent)) {
                        luggageAgents.add(agent);
                        System.out.println("New luggage-agent in town: " + agent.getLocalName() + ", now have " + luggageAgents.size());
                    }
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}
