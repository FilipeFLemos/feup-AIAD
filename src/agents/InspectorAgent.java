package agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;

import java.util.Random;
import java.util.Vector;

public class InspectorAgent extends AbstractAgent {

    private double distance;
    private boolean isBusy;

    public InspectorAgent() {
        state = State.IDLE;
        setPeopleScanAgents(new Vector<>());
        distance = randomNumber(1000);
        System.out.println("Distance " + distance);
    }

    public double randomNumber(int i) {
        Random rand = new Random();
        return Math.round((rand.nextFloat() * i) * 100.0) / 100.0;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Inspector-agent " + getAID().getName() + " is ready.");
        setServiceDescription("inspector");

        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));

        addBehaviour(new ClosestInspectorAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));

    }

    public double getInspectorDistance() {
        return distance;
    }

    // public boolean getIsBusy() {
    // return isBusy;
    // }

    // public void setIsBusy(boolean isBusy) {
    // this.isBusy = isBusy;
    // }

    // public void toggleIsBusy() {
    // this.isBusy = !this.isBusy;
    // }

}
