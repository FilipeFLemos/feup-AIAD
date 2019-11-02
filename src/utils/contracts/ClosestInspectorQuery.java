package utils.contracts;

import agents.AbstractAgent;
import agents.InspectorAgent;
import agents.LuggageAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Utils;

import java.io.IOException;
import java.util.Vector;

public class ClosestInspectorQuery extends ContractNetInitiator {

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
        // AbstractAgent abstractAgent = (AbstractAgent) myAgent;
        AbstractAgent inspectorAgent = (AbstractAgent) myAgent;

        double min = Utils.MAX_INSPECTOR_DISTANCE;
        System.out.println("double");
        for (Object response : responses) {
            double curr = Utils.MAX_INSPECTOR_DISTANCE;
            try {
                if (null != (Double) ((ACLMessage) response).getContentObject()) {
                    curr = (Double) ((ACLMessage) response).getContentObject();
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            if (curr < min)
                min = curr;
        }

        boolean chosen = false;
        for (Object response : responses) {
            ACLMessage current = (ACLMessage) response;
            try {
                ACLMessage msg = current.createReply();
                if (null != ((ACLMessage) response).getContentObject()) {

                    if (!chosen && (Double) ((ACLMessage) response).getContentObject() == min) {
                        msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        try {
                            System.out.println("Entras? " + inspectorAgent.getPerson());
                            msg.setContentObject(inspectorAgent.getPerson());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        chosen = true;
                        inspectorAgent.movedPerson();
                    }

                    else
                        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }

                acceptances.add(msg);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }
    }
}