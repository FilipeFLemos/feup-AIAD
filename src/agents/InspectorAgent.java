package agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;

import java.util.Random;
import java.util.Vector;

public class InspectorAgent extends AbstractAgent {

    private int distance;
    private boolean isBusy;

    public InspectorAgent() {
        setPeopleScanAgents(new Vector<>());
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
        setServiceDescription("inspector");

        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));

        addBehaviour(new ClosestInspectorAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));

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
