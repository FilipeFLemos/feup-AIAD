package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.QueueSizeAnswerer;

import java.awt.*;

public class PeopleScanAgent extends AbstractAgent {

    public int totalTime = 0;
    public int numPeople = 0;

    public PeopleScanAgent() {
        state = State.IDLE;
    }

    public PeopleScanAgent(int x, int y){
        state = State.IDLE;
        location = new Point(x,y);
    }

    @Override
    protected void setup() {
        //parseArgs();

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
                    myAgent.addBehaviour(new WakerBehaviour(myAgent,Utils.SCANNING_TIME) {
                        @Override
                        protected void onWake() {
                            Person person = (Person) agentQueue.poll();
                            person.stopTimer();
                            int time = (int) person.getTotalWaitingTime();
                            totalTime += time;
                            numPeople++;
                            System.out.println(myAgent.getLocalName() + ": Finished scanning Person (ID: "
                                    + person.getId() + ")");
                            System.out.println(myAgent.getLocalName() + ": Person ID: "
                                    + person.getId() + " spent " +time + " seconds on the system!");
                            state = State.IDLE;
                        }
                    });
                }
            }

        }
    }
}
