package utils.contracts;

import agents.AbstractAgent;
import agents.InspectorAgent;
import agents.LuggageAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import java.util.Vector;

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
            int distance = inspectorAgent.getInspectorDistance();
            if (!inspectorAgent.getIsBusy()) {
                reply.setContentObject(distance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reply;

    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

        ACLMessage reply = accept.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("Will be done");
        ((InspectorAgent) myAgent).toggleIsBusy();

        System.out.println(myAgent.getLocalName() + ": I was selected to check the irregularity from Agent "
                + cfp.getSender().getLocalName());

        return reply;
    }

}
