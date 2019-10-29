package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import utils.contracts.LateInspectorAgentSubscription;
import utils.contracts.LateLuggageAgentSubscription;
import utils.contracts.LateScanAgentSubscription;
import utils.contracts.QueueSizeQuery;
//import utils.contracts.ClosestInspectorQuery.java;

import java.util.Vector;

public class Utils {

    public static final int NUM_LUGGAGE_AGENTS = 3;
    public static int NUM_PEOPLE_AGENTS = 2;
    public static int NUM_INSPECTOR_AGENTS = 3;
    public static int MAX_LUGGAGE_CAPACITY = 5;
    public static int MAX_PEOPLE_QUEUE_SIZE = 100;
    public static int MAX_INSPECTOR_DISTANCE = 1000;

    public static DFAgentDescription getDFAgentDescriptionTemplate(String type) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        return template;
    }

    public static Vector<AID> findAvailableAgents(Agent agent, String type) {
        Vector<AID> agents = new Vector<>();

        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate(type);
        try {
            DFAgentDescription[] result = DFService.search(agent, template);
            System.out.println(agent.getLocalName() + ": Found " + result.length + " " + type + " Agents.");
            for (DFAgentDescription agentDescription : result) {
                agents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return agents;
    }

    public static void allocatePersonToBeScanned(Agent agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("What is your queue size?");
        agent.addBehaviour(new QueueSizeQuery(agent, msg, "scan"));
    }

    public static SubscriptionInitiator lateSubscriptionFactoryMethod(Agent agent, String agentType){
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate(agentType);
        switch (agentType)
        {
            case "luggage":
                return new LateLuggageAgentSubscription(agent, template);
            case "scan":
                return new LateScanAgentSubscription(agent, template);
            case "inspector":
                return new LateInspectorAgentSubscription(agent, template);
            default:
                return new SubscriptionInitiator(agent,null);
        }
    }
}
