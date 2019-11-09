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
import java.awt.Point;

public class InspectorAgent extends AbstractAgent {

    public InspectorAgent() {
        state = State.IDLE;
        location = new Point(0, 0);
        setPeopleScanAgents(new Vector<>());
    }

    public InspectorAgent(int x, int y) {
        this();
        location = new Point(x, y);
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

    private class InspectLuggage extends CyclicBehaviour {
        private Person person;

        public void action() {
            if (state == State.IDLE) {

                if (agentQueue.isEmpty()) {
                    block();
                } else {
                    state = State.MOVING;
                    person = (Person) agentQueue.peek();
                    System.out.println("Location: " + person.getLocation());
                    update();

                }
            } else if (state == State.MOVING && getInspectorDistance() == 0) {
                state = State.WORKING;
                Person person = ((Person) agentQueue.peek());
                setLocation(person.getLocation());
                System.out.println(myAgent.getLocalName() + ": Going to start inspect the luggage of Person (ID: "
                        + person.getId() + ")");
                myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.LUGGAGE_PROCESSING_TIME)) {
                    @Override
                    protected void onWake() {
                        Person person = (Person) agentQueue.peek();
                        System.out.println(myAgent.getLocalName() + ": Finished inspecting the luggage of Person (ID: "
                                + ((Person) agentQueue.peek()).getId() + ")");
                        state = State.IDLE;
                        person = null;
                        agentQueue.poll();
                    }

                });
                state = State.IDLE;
                // setInspectorDistance(randomNumber(100));
            } else if (state == State.MOVING)
                update();
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
            // System.out.println(newDistance);

        }
    }

}
