package utils.contracts;

import agents.InspectorAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import models.Person;

import java.io.IOException;

public class ClosestInspectorAnswerer extends ContractNetResponder {

    public ClosestInspectorAnswerer(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        InspectorAgent inspectorAgent = (InspectorAgent) myAgent;
        ACLMessage reply = cfp.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        try {
            double distance = inspectorAgent.getInspectorDistance();
            reply.setContentObject(distance);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reply;

    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

        InspectorAgent inspectorAgent = ((InspectorAgent) myAgent);

        ACLMessage reply = accept.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("Will be done");
        try {
            Person person = (Person) accept.getContentObject();
            System.out.println(" Accept " + accept.getContentObject());
            System.out.println("Person " + person);
            inspectorAgent.enqueue(person);
            System.out.println("Agent local name " + myAgent.getLocalName());
            System.out.println("Person ID " + person.getId());
            System.out.println("From Agent " + cfp.getSender().getLocalName());
            System.out.println(myAgent.getLocalName() + ": I was selected to check the person's (ID: " + person.getId()
                    + ") irregularity from Agent " + cfp.getSender().getLocalName());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        return reply;
    }

}
