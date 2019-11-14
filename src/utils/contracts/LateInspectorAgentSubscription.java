package utils.contracts;

import agents.AbstractAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class LateInspectorAgentSubscription extends SubscriptionInitiator {

    public LateInspectorAgentSubscription(Agent agent, DFAgentDescription dfad) {
        super(agent, DFService.createSubscriptionMessage(agent, agent.getDefaultDF(), dfad, null));
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        try {
            DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
            for (DFAgentDescription dfd : dfds) {
                AID agent = dfd.getName();
                AbstractAgent abstractAgent = (AbstractAgent) myAgent;
                if (!abstractAgent.getInspectorAgents().contains(agent)) {
                    abstractAgent.getInspectorAgents().add(agent);
                    System.out.println(myAgent.getLocalName() + ": New inspector-agent in town: " + agent.getLocalName() + ", now have " + abstractAgent.getInspectorAgents().size());
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
