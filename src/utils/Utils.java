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

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.Point;
import java.lang.Math;
//import utils.contracts.ClosestInspectorQuery.java;

public class Utils {

    public static final int NUM_LUGGAGE_AGENTS = 0;
    public static int NUM_PEOPLE_AGENTS = 0;
    public static int NUM_INSPECTOR_AGENTS = 0;
    public static int MAX_LUGGAGE_CAPACITY = 1;
    public static int MAX_PEOPLE_QUEUE_SIZE = 100;
    public static float MAX_INSPECTOR_DISTANCE = 1000f;
    public static int QUEUE_MIN_FREQUENCY = 5;
    public static int QUEUE_MAX_FREQUENCY = 10;
    public static int MAX_THREADS = 30;
    public static int LUGGAGE_PROCESSING_TIME = 10;
    public static int INSPECTOR_PROCESSING_TIME = 10;
    public static int REQUERY_DELAY = 1;
    public static int SCANNING_TIME = 5;
    public static float INSPECTOR_SPEED = 1.4f;
    public static double MAX_INSPECTOR_BUSYTIME = 3600;

    public static Point[] INSPECTOR_AGENTS_LOCATION = new Point[] { new Point(3, 5), new Point(5, 8), new Point(5, 5) };
    public static Point[] LUGGAGE_AGENTS_LOCATION = new Point[] { new Point(0, 0), new Point(4, 0), new Point(8, 0) };
    public static Point[] PEOPLE_AGENTS_LOCATION = new Point[] { new Point(7, 7), new Point(7, 0) };

    /**
     * Generates a random number.
     *
     * @param min the min - the minimum number
     * @param max the max - the maximum number
     * @return the random number between them
     */
    public static int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int getMilliSeconds(int seconds) {
        return seconds * 1000;
    }

    private static DFAgentDescription getDFAgentDescriptionTemplate(String type) {
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

    public static SubscriptionInitiator lateSubscriptionFactoryMethod(Agent agent, String agentType) {
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate(agentType);
        switch (agentType) {
            case "luggage":
                return new LateLuggageAgentSubscription(agent, template);
            case "scan":
                return new LateScanAgentSubscription(agent, template);
            case "inspector":
                return new LateInspectorAgentSubscription(agent, template);
            default:
                return new SubscriptionInitiator(agent, null);
        }
    }

    public static double distance(Point p1, Point p2) {
        return Math.sqrt(
                (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()) + (p2.getX() - p1.getX()) * (p2.getX() - p1.getX()));

    }

}