package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;

import java.util.Vector;
import java.util.Random;

public class InspectorAgent extends Agent {

    private Vector<AID> peopleScanAgents;
    private int distance;
    private boolean isBusy;

    public InspectorAgent() {

        peopleScanAgents = new Vector<>();
        distance = randomNumber(51);
        System.out.println("Distance " + distance);
        isBusy = (randomNumber(101) < 50) ? true : false;
    }

    public int randomNumber(int i) {
        Random rand = new Random();
        return rand.nextInt(i);
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Inspector-agent " + getAID().getName() + " is ready.");
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("inspector");
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        Utils.acceptNewAgents(this, "scan");

        addBehaviour(new ClosestInspectorAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));

    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

    public int getInspectorDistance() {
        return distance;
    }

    public boolean getIsBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void toggleIsBusy() {
        this.isBusy = !this.isBusy;
    }

}
