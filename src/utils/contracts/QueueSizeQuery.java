package utils.contracts;

import agents.AbstractAgent;
import agents.LuggageAgent;
import agents.QueueManagerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Utils;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.Utils.MAX_THREADS;

public class QueueSizeQuery extends ContractNetInitiator {

    String agentType;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(MAX_THREADS);

    public QueueSizeQuery(Agent a, ACLMessage msg, String agentType) {
        super(a, msg);
        this.agentType = agentType;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> v = new Vector<>();

        QueueManagerAgent queueManagerAgent = null;
        LuggageAgent luggageAgent = null;
        if (myAgent instanceof QueueManagerAgent) {
            queueManagerAgent = (QueueManagerAgent) myAgent;
        } else {
            luggageAgent = (LuggageAgent) myAgent;
        }


        switch (agentType) {
            case "luggage":
                for (AID aid : ((QueueManagerAgent) myAgent).getLuggageAgents()) {
                    cfp.addReceiver(aid);
                }
                break;
            case "scan":

                if (luggageAgent != null) {
                    for (AID aid : luggageAgent.getPeopleScanAgents()) {
                        cfp.addReceiver(aid);
                    }
                } else {
                    for (AID aid : queueManagerAgent.getPeopleScanAgents()) {
                        cfp.addReceiver(aid);
                    }
                }

                break;
        }

        v.add(cfp);
        return v;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        AbstractAgent abstractAgent = (AbstractAgent) myAgent;

        int maxQueueSize = 0;
        switch (agentType) {
            case "luggage":
                maxQueueSize = Utils.MAX_LUGGAGE_CAPACITY;
                break;
            case "scan":
                maxQueueSize = Utils.MAX_PEOPLE_QUEUE_SIZE;
                break;
        }

        int min = maxQueueSize;
        for (Object response : responses) {
            int curr = maxQueueSize;
            try {
                curr = (Integer) ((ACLMessage) response).getContentObject();
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
                if (!chosen && (Integer) current.getContentObject() == min) {
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    try {
                        msg.setContentObject(abstractAgent.getPerson());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    chosen = true;
                    abstractAgent.movedPerson();
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