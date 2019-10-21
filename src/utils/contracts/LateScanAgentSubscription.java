package utils.contracts;

import agents.LuggageAgent;
import agents.QueueManagerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class LateScanAgentSubscription extends SubscriptionInitiator {

    public LateScanAgentSubscription(Agent agent, DFAgentDescription dfad) {
        super(agent, DFService.createSubscriptionMessage(agent, agent.getDefaultDF(), dfad, null));
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        try {
            DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
            for (DFAgentDescription dfd : dfds) {
                AID agent = dfd.getName();
                LuggageAgent luggageAgent = (LuggageAgent) myAgent;
                if (!luggageAgent.getPeopleScanAgents().contains(agent)) {
                    luggageAgent.getPeopleScanAgents().add(agent);
                    System.out.println("New people-scan-agent in town: " + agent.getLocalName() + ", now have " + luggageAgent.getPeopleScanAgents().size());
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
