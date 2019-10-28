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

        for (Object response : responses) {
            ACLMessage current = (ACLMessage) response;
            try {
                ACLMessage msg = current.createReply();
                if ((Integer) ((ACLMessage) response).getContentObject() != -1) {
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.add(msg);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

        }
    }
}