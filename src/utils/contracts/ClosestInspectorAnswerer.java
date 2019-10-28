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
        LuggageAgent luggageAgent = (LuggageAgent) myAgent;
        Vector<AID> inspectorAgents = (Vector<AID>) luggageAgent.getInspectorAgents();
        int pos = 0;
        int minDistance = 1000;
        int minPos = -1;
        for (Object inspector : inspectorAgents) {
            InspectorAgent inspectorAgent = (InspectorAgent) inspector;
            int distance = inspectorAgent.getInspectorDistance();
            if (distance < minDistance && !inspectorAgent.getIsBusy()) {
                minDistance = distance;
                minPos = pos;
            }
            pos++;
        }

        ACLMessage reply = cfp.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        System.out.println("Hey " + minPos);
        // if (minPos != -1) {
        // InspectorAgent inspectorAgent = (InspectorAgent) inspectorAgents.get(minPos);
        // inspectorAgent.toggleIsBusy();
        // }
        try {
            reply.setContentObject(minPos);
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
        ((AbstractAgent) myAgent).increaseQueueSize();

        System.out.println(myAgent.getLocalName() + ": I was selected to take the next person from Agent "
                + cfp.getSender().getLocalName());

        return reply;
    }

}
