package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.proto.SubscriptionInitiator;
import utils.Utils;

import java.util.Vector;

public class QueueManagerAgent extends Agent {

    private Vector<AID> luggageAgents;
    private Vector<AID> peopleScanAgents;

    public QueueManagerAgent(){
        luggageAgents = new Vector<>();
        peopleScanAgents = new Vector<>();
    }

    @Override
    protected void setup() {
        findAgents();
        lateAgentSubscription();
    }

    private void findAgents() {
        DFAgentDescription template = getDFAgentDescriptionTemplate("luggage");
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found " + result.length + " Luggage Control Agents.");
            for (DFAgentDescription agentDescription : result) {
                luggageAgents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        template = getDFAgentDescriptionTemplate("peopleScan");
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found " + result.length + " People Scan Agents.");
            for (DFAgentDescription agentDescription : result) {
                peopleScanAgents.add(agentDescription.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private DFAgentDescription getDFAgentDescriptionTemplate(String type) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        return template;
    }

    private void lateAgentSubscription() {
        DFAgentDescription template = getDFAgentDescriptionTemplate("luggage");
        addBehaviour(new LuggageAgentSubscription(this, template));
    }

    public void allocatePerson() {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("How many luggage can you receive?");
        addBehaviour(new TrailSizeQuery(this, msg));
    }

    public Vector<AID> getLuggageAgents() {
        return luggageAgents;
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

    //Private class responsible for task allocation
    private class TrailSizeQuery extends ContractNetInitiator {

        TrailSizeQuery(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector<ACLMessage> v = new Vector<>();

            for (AID aid : ((QueueManagerAgent) myAgent).getLuggageAgents()) {
                cfp.addReceiver(aid);
            }

            v.add(cfp);
            return v;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {

            int min = Utils.MAX_LUGGAGE_CAPACITY;
            for (Object response : responses) {
                int curr = Utils.MAX_LUGGAGE_CAPACITY;
                try {
                    curr = (Integer) ((ACLMessage) response).getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                if (curr < min) min = curr;
            }

            boolean chosen = false;
            for (Object response : responses) {
                ACLMessage current = (ACLMessage) response;
                try {
                    ACLMessage msg = current.createReply();
                    if (!chosen && (Integer) current.getContentObject() == min) {
                        msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        chosen = true;
                    } else {
                        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    }
                    acceptances.add(msg);
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            //System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }

    //Private class responsible for being alert to late new agents
    private class LuggageAgentSubscription extends SubscriptionInitiator {

        LuggageAgentSubscription(Agent agent, DFAgentDescription dfad) {
            super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                for (DFAgentDescription dfd : dfds) {
                    AID agent = dfd.getName();
                    if (!luggageAgents.contains(agent)) {
                        luggageAgents.add(agent);
                        System.out.println("New luggage-agent in town: " + agent.getLocalName() + ", now have " + luggageAgents.size());
                    }
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}
