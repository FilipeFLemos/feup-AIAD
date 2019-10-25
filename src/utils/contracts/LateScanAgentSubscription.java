package utils.contracts;

import agents.InspectorAgent;
import agents.LuggageAgent;
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
                if(myAgent instanceof InspectorAgent){
                    inspectorAgent = (InspectorAgent) myAgent;
                }
                else{
                    luggageAgent = (LuggageAgent) myAgent;
                }

                if (luggageAgent != null && !luggageAgent.getPeopleScanAgents().contains(agent)) {

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
