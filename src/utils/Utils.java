package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import utils.contracts.LateSubscription;

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

    public static Vector<AID> findScanAgents(Agent agent){
        Vector<AID> agents = new Vector<>();

        DFAgentDescription template = Utils.getDFAgentDescriptionTemplate("scan");
        try {
            DFAgentDescription[] result = DFService.search(agent, template);
            System.out.println("Found " + result.length + " People Scan Agents.");
            for (DFAgentDescription agentDescription : result) {
                agents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return agents;
    }
}
