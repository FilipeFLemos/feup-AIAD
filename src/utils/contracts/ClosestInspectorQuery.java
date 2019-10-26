package utils.contracts;

import agents.InspectorAgent;
import agents.LuggageAgent;
import agents.QueueManagerAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Utils;

import java.util.Vector;

public class ClosestInspectorQuery extends ContractNetInitiator {

    String agentType;

    public ClosestInspectorQuery(Agent a, ACLMessage msg) {
        super(a, msg);
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> v = new Vector<>();
        LuggageAgent luggageAgent = (LuggageAgent) myAgent;
        for (AID aid : luggageAgent.getInspectorAgents()) {
            cfp.addReceiver(aid);
        }

        v.add(cfp);
        return v;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        LuggageAgent luggageAgent = (LuggageAgent) myAgent;
        Vector<AID> inspectorAgents = (Vector<AID>) luggageAgent.getInspectorAgents();
        int pos = 0;
        int minDistance = 1000;
        int minPos = -1;
        for (Object inspectorAgent : inspectorAgents) {
            InspectorAgent inspector = (InspectorAgent) inspectorAgent;

            int distance = inspector.getInspectorDistance();
            if (distance < minDistance && !inspector.getIsBusy()) {
                minDistance = distance;
                minPos = pos;
            }

            pos++;
        }

        for (Object response : responses) {
            ACLMessage current = (ACLMessage) response;

            ACLMessage msg = current.createReply();
            if (minPos != -1) {
                msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
            } else {
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
            acceptances.add(msg);

        }
    }
}