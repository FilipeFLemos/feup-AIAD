package utils.contracts;

import agents.AbstractAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import models.Person;

import java.io.IOException;

public class QueueSizeAnswerer extends ContractNetResponder {

    private int maxQueueSize;

    public QueueSizeAnswerer(Agent a, MessageTemplate mt, int maxQueueSize) {
        super(a, mt);
        this.maxQueueSize = maxQueueSize;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        AbstractAgent abstractAgent = (AbstractAgent) myAgent;
        ACLMessage reply = cfp.createReply();

        int queueSpace = abstractAgent.getQueueSize();
        if(queueSpace == maxQueueSize - 1){
            reply.setPerformative(ACLMessage.REFUSE);
        }
        else{
            reply.setPerformative(ACLMessage.PROPOSE);
            try {
                reply.setContentObject(queueSpace);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return reply;
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

        ACLMessage reply = accept.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("Will be done");
        ((AbstractAgent) myAgent).increaseQueueSize();

        try {
            Person person = (Person) accept.getContentObject();
            System.out.println(myAgent.getLocalName() + ": I was selected to take the next person (ID: " + person.getId() + ") from Agent "
                    + cfp.getSender().getLocalName());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }


        return reply;
    }
}
