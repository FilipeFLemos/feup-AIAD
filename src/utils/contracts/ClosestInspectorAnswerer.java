package utils.contracts;

import agents.AbstractAgent;
import agents.InspectorAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import models.Person;

import java.io.IOException;
import java.awt.Point;

public class ClosestInspectorAnswerer extends ContractNetResponder {

    public ClosestInspectorAnswerer(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        AbstractAgent a = (AbstractAgent) myAgent;
        ACLMessage reply = cfp.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        try {
            reply.setContentObject(new Point(1, 1));
            // Aqui precisava de dar setContentObject da location do sender (tipo, o
            // luggageControl1 dava a location para depois no handleAllReponses, fazer a
            // distance)
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
            inspectorAgent.enqueue(person);
            System.out.println(myAgent.getLocalName() + ": I was selected to check the person's (ID: " + person.getId()
                    + ") irregularity from Agent " + cfp.getSender().getLocalName());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        return reply;
    }

}
