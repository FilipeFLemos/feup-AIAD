package utils.contracts;

import agents.QueueManagerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class LateLuggageAgentSubscription extends SubscriptionInitiator {

    public LateLuggageAgentSubscription(Agent agent, DFAgentDescription dfad) {
        super(agent, DFService.createSubscriptionMessage(agent, agent.getDefaultDF(), dfad, null));
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        try {
            DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
            for (DFAgentDescription dfd : dfds) {
                AID agent = dfd.getName();
                QueueManagerAgent queueManagerAgent = (QueueManagerAgent) myAgent;

                if (!queueManagerAgent.getLuggageAgents().contains(agent)) {
                    queueManagerAgent.getLuggageAgents().add(agent);
                    System.out.println(myAgent.getLocalName() + ": New luggage-agent in town: " + agent.getLocalName() + ", now have " + queueManagerAgent.getLuggageAgents().size());
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
