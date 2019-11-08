package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;

import java.util.Random;
import java.util.Vector;

public class InspectorAgent extends AbstractAgent {

    private double distance;

    public InspectorAgent() {
        state = State.IDLE;
        setPeopleScanAgents(new Vector<>());
        distance = randomNumber(100);
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
        addBehaviour(new InspectLuggage());

    }

    public double getInspectorDistance() {
        return distance;
    }

    public void setInspectorDistance(double distance) {
        this.distance = distance;
    }

    private class InspectLuggage extends CyclicBehaviour {
        private Person person;

        public void action() {

            if (state == State.IDLE) {

                if (agentQueue.isEmpty()) {
                    block();
                } else {
                    state = State.MOVING;
                    update();

                }
            } else if (state == State.MOVING && getInspectorDistance() == 0) {
                state = State.WORKING;
                System.out.println(myAgent.getLocalName() + ": Going to start inspect the luggage of Person (ID: "
                        + ((Person) agentQueue.peek()).getId() + ")");
                myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.LUGGAGE_PROCESSING_TIME)) {
                    @Override
                    protected void onWake() {
                        person = (Person) agentQueue.peek();
                        System.out.println(myAgent.getLocalName() + ": Finished inspecting the luggage of Person (ID: "
                                + ((Person) agentQueue.peek()).getId() + ")");
                        state = State.IDLE;
                        person = null;
                        agentQueue.poll();
                    }

                });
            }
        }

        public void update() {
            try {
                Thread.sleep(Utils.SECOND);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            double newDistance = getInspectorDistance() - Utils.INSPECTOR_SPEED;
            newDistance = (newDistance > 0) ? newDistance : 0;
            setInspectorDistance(newDistance);

        }
    }

}
