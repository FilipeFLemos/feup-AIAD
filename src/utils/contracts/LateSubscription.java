package utils.contracts;

import agents.QueueManagerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class LateSubscription extends SubscriptionInitiator {

    String agentType;

    public LateSubscription(Agent agent, DFAgentDescription dfad, String agentType) {
        super(agent, DFService.createSubscriptionMessage(agent, agent.getDefaultDF(), dfad, null));
        this.agentType = agentType;
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        try {
            DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
            for (DFAgentDescription dfd : dfds) {
                AID agent = dfd.getName();
                QueueManagerAgent queueManagerAgent = (QueueManagerAgent) myAgent;

                switch(agentType){
                    case "luggage":
                        if (!queueManagerAgent.getLuggageAgents().contains(agent)) {
                            queueManagerAgent.getLuggageAgents().add(agent);
                            System.out.println(myAgent.getLocalName() + ": New luggage-agent in town: " + agent.getLocalName() + ", now have " + queueManagerAgent.getLuggageAgents().size());
                        }
                        break;
                    case "scan":
                        if (!queueManagerAgent.getPeopleScanAgents().contains(agent)) {
                            queueManagerAgent.getPeopleScanAgents().add(agent);
                            System.out.println(myAgent.getLocalName() + ": New people-scan-agent in town: " + agent.getLocalName() + ", now have " + queueManagerAgent.getPeopleScanAgents().size());
                        }
                        break;
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
