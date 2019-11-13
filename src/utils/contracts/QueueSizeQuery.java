package utils.contracts;

import agents.AbstractAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Utils;

import java.io.IOException;
import java.util.Vector;

public class QueueSizeQuery extends ContractNetInitiator {

    private ACLMessage msg;
    private String agentType;

    public QueueSizeQuery(Agent a, ACLMessage msg, String agentType) {
        super(a, msg);
        this.agentType = agentType;
        this.msg = msg;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> v = new Vector<>();
        AbstractAgent abstractAgent = (AbstractAgent) myAgent;

        switch (agentType) {
            case "luggage":
                for (AID aid : abstractAgent.getLuggageAgents()) {
                    cfp.addReceiver(aid);
                }
                break;
            case "scan":
                for (AID aid : abstractAgent.getPeopleScanAgents()) {
                    cfp.addReceiver(aid);
                }
                break;
        }

        v.add(cfp);
        return v;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        AbstractAgent abstractAgent = (AbstractAgent) myAgent;
        int maxQueueSize = Utils.MAX_PEOPLE_QUEUE_SIZE;

        int min = maxQueueSize;
        for (ACLMessage response : (Vector<ACLMessage>) responses) {
            if (response.getPerformative() != ACLMessage.PROPOSE) {
                continue;
            }
            int curr = maxQueueSize;
            try {
                curr = (Integer) response.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            if (curr < min)
                min = curr;
        }

        boolean chosen = false;
        for (ACLMessage response : (Vector<ACLMessage>) responses) {
            if (response.getPerformative() != ACLMessage.PROPOSE) {
                continue;
            }
            try {
                ACLMessage msg = response.createReply();
                if (!chosen && (Integer) response.getContentObject() == min) {
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    try {
                        msg.setContentObject(abstractAgent.getPerson());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    chosen = true;
                    abstractAgent.movedPerson();
                    abstractAgent.setStateIdle();
                } else {
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.add(msg);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        if (min == maxQueueSize) {
            System.out.println("Retrying...");
            rerunBehaviour();
        }
    }

    private void rerunBehaviour() {
        myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.REQUERY_DELAY)) {
            @Override
            protected void onWake() {
                myAgent.addBehaviour(new QueueSizeQuery(myAgent, msg, agentType));
            }
        });
    }
}