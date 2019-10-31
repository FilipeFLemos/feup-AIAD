package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.QueueSizeAnswerer;

public class PeopleScanAgent extends AbstractAgent {

    public PeopleScanAgent() {
        state = State.IDLE;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! PeopleScan-agent " + getAID().getName() + " is ready.");
        setServiceDescription("scan");

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP), Utils.MAX_PEOPLE_QUEUE_SIZE));
        addBehaviour(new ScanPeople());
    }


    private class ScanPeople extends CyclicBehaviour {
        public void action() {

            if (state == State.IDLE) {
                if (agentQueue.isEmpty()) {
                    block();
                } else {
                    state = State.WORKING;
                    myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.SCANNING_TIME)) {
                        @Override
                        protected void onWake() {
                            Person person = (Person) agentQueue.poll();
                            person.stopTimer();
                            System.out.println("Finished scanning");
                            state = State.IDLE;
                        }
                    });
                }
            }

        }
    }
}
