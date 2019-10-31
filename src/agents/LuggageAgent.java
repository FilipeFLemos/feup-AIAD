package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.ClosestInspectorQuery;
import utils.contracts.QueueSizeAnswerer;

import java.util.Random;
import java.util.Vector;

public class LuggageAgent extends AbstractAgent {

    private Vector<AID> inspectorAgents;
    private boolean hasIrregularLuggage;

    public LuggageAgent() {
        setPeopleScanAgents(new Vector<>());
        hasIrregularLuggage = randomizeHasIrregularLuggage();
        state = State.IDLE;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Luggage-agent " + getAID().getName() + " is ready.");
        setServiceDescription("luggage");

        findAvailableAgents();
        acceptNewAgents();

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP), Utils.MAX_LUGGAGE_CAPACITY));
        addBehaviour(new ScanLuggage());
        //Utils.allocatePersonToBeScanned(this);
        //System.out.println("Irreg " + getHasIrregularLuggage());
        /*if (getHasIrregularLuggage())
            allocateClosestInspector(this);*/
    }

    private void findAvailableAgents() {
        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        inspectorAgents = Utils.findAvailableAgents(this, "inspector");
    }

    private void acceptNewAgents() {
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "inspector"));
    }

    private void allocateClosestInspector(Agent agent) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("Which Inspector is the closest?");
        agent.addBehaviour(new ClosestInspectorQuery(agent, msg));
    }

    public Vector<AID> getInspectorAgents() {
        return inspectorAgents;
    }

    public boolean getHasIrregularLuggage() {
        return hasIrregularLuggage;
    }

    public void setHasIrregularLuggage(boolean isIrregular) {
        hasIrregularLuggage = isIrregular;
    }

    public boolean randomizeHasIrregularLuggage() {
        Random rand = new Random();
        if (rand.nextInt(101) > 90) {
            return true;
        }
        return false;
    }

    private class ScanLuggage extends CyclicBehaviour {
        public void action() {

            if (state == State.IDLE) {
                if (agentQueue.isEmpty()) {
                    block();
                } else {
                    state = State.WORKING;
                    myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.LUGGAGE_PROCESSING_TIME)) {
                        @Override
                        protected void onWake() {
                            Person person = (Person) agentQueue.peek();
                            //TODO: if something smelly chamar inspector

                            //If everything is okay
                            person.stopTimer();
                            System.out.println("Finished scanning luggage");
                            state = State.IDLE;
                            agentQueue.poll();
                        }
                    });
                }
            }
        }
    }
}
