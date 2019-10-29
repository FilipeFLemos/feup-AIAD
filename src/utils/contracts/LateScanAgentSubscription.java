package utils.contracts;

import agents.InspectorAgent;
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

                InspectorAgent inspectorAgent = null;
                LuggageAgent luggageAgent = null;
                QueueManagerAgent queueManagerAgent = null;
                if(myAgent instanceof InspectorAgent){
                    inspectorAgent = (InspectorAgent) myAgent;
                }
                else if(myAgent instanceof LuggageAgent){
                    luggageAgent = (LuggageAgent) myAgent;
                }
                else {
                    queueManagerAgent = (QueueManagerAgent) myAgent;
                }

                if (queueManagerAgent != null && !queueManagerAgent.getPeopleScanAgents().contains(agent)) {
                    queueManagerAgent.getPeopleScanAgents().add(agent);
                    System.out.println(myAgent.getLocalName() + ": New people-scan-agent in town: " + agent.getLocalName() + ", now have " + queueManagerAgent.getPeopleScanAgents().size());
                }
                else if (luggageAgent != null && !luggageAgent.getPeopleScanAgents().contains(agent)) {
                    luggageAgent.getPeopleScanAgents().add(agent);
                    System.out.println(myAgent.getLocalName() + ": New people-scan-agent in town: " + agent.getLocalName() + ", now have " + luggageAgent.getPeopleScanAgents().size());
                }
                else if(inspectorAgent != null && !inspectorAgent.getPeopleScanAgents().contains(agent)){
                    inspectorAgent.getPeopleScanAgents().add(agent);
                    System.out.println(myAgent.getLocalName() + ": New people-scan-agent in town: " + agent.getLocalName() + ", now have " + inspectorAgent.getPeopleScanAgents().size());
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
