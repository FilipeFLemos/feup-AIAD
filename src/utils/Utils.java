package utils;

import agents.InspectorAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.contracts.ClosestInspectorQuery;
import utils.contracts.LateScanAgentSubscription;
import utils.contracts.LateSubscription;
import utils.contracts.QueueSizeQuery;
//import utils.contracts.ClosestInspectorQuery.java;

import java.util.Vector;

public class Utils {

    public static final int NUM_LUGGAGE_AGENTS = 3;
    public static int NUM_PEOPLE_AGENTS = 2;
    public static int NUM_INSPECTOR_AGENTS = 1;
    public static int MAX_LUGGAGE_CAPACITY = 5;
    public static int MAX_PEOPLE_QUEUE_SIZE = 100;

    public static DFAgentDescription getDFAgentDescriptionTemplate(String type) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        return template;
    }

    /**
     * public static Vector<AID> findAvailableScanAgents(Agent agent){ Vector<AID>
     * agents = new Vector<>();
     * 
     * DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("scan");
     * try { DFAgentDescription[] result = DFService.search(agent, template);
     * System.out.println(agent.getLocalName() + ": Found " + result.length + "
     * People Scan Agents."); for (DFAgentDescription agentDescription : result) {
     * agents.add(agentDescription.getName()); } } catch (FIPAException fe) {
     * fe.printStackTrace(); }
     * 
     * return agents; }
     * 
     * public static Vector<AID> findAvailableInspectorAgents(Agent agent){
     * Vector<AID> agents = new Vector<>();
     * 
     * DFAgentDescription template =
     * Utils.getDFAgentDescriptionTemplate("inspector"); try { DFAgentDescription[]
     * result = DFService.search(agent, template);
     * System.out.println(agent.getLocalName() + ": Found " + result.length + "
     * Inspector Agents."); for (DFAgentDescription agentDescription : result) {
     * agents.add(agentDescription.getName()); } } catch (FIPAException fe) {
     * fe.printStackTrace(); }
     * 
     * return agents; }
     **/

    public static Vector<AID> findAvailableAgents(Agent agent, String type) {
        Vector<AID> agents = new Vector<>();

        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate(type);
        try {
            DFAgentDescription[] result = DFService.search(agent, template);
            if (type == "scan") {
                System.out.println(agent.getLocalName() + ": Found " + result.length + " " + type + " Agents.");
                for (DFAgentDescription agentDescription : result) {
                    agents.add(agentDescription.getName());
                }
            } else if (type == "inspector") {
                InspectorAgent inspectorAgent = null;
                for (DFAgentDescription agentDescription : result) {
                    agents.add(agentDescription.getName());
                }

            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return agents;
    }

    /**
     * public static void acceptNewScanAgents(Agent agent) { DFAgentDescription
     * template = Utils.getDFAgentDescriptionTemplate("scan");
     * agent.addBehaviour(new LateScanAgentSubscription(agent, template)); }
     * 
     * public static void acceptNewInspectorAgents(Agent agent) { DFAgentDescription
     * template = Utils.getDFAgentDescriptionTemplate("inspector");
     * agent.addBehaviour(new LateScanAgentSubscription(agent, template)); }
     **/
    public static void acceptNewAgents(Agent agent, String type) {
        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate(type);
        agent.addBehaviour(new LateScanAgentSubscription(agent, template));
    }

    public static void allocatePersonToBeScanned(Agent agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("What is your queue size?");
        agent.addBehaviour(new QueueSizeQuery(agent, msg, "scan"));
        ;
    }

    public static void allocateClosestInspector(Agent agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("Which Inspector is the closest?");
        agent.addBehaviour(new ClosestInspectorQuery(agent, msg));
    }

}
