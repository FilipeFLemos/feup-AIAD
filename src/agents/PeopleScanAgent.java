package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.QueueSizeAnswerer;

public class PeopleScanAgent extends AbstractAgent {

    @Override
    protected void setup() {
        System.out.println("Hallo! PeopleScan-agent " + getAID().getName() + " is ready.");
        setServiceDescription("scan");

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP), Utils.MAX_PEOPLE_QUEUE_SIZE));
        addBehaviour(new ScanPeople());
    }


    private class ScanPeople extends CyclicBehaviour {
        public void action() {

            if(agentQueue.isEmpty()){
                block();
            }
            else{
                Person person = (Person) agentQueue.poll();

                int milliseconds = Utils.getMilliSeconds(Utils.SCANNING_TIME);
//                try {
//                    Thread.sleep(milliseconds);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                person.stopTimer();
                System.out.println("Finished scanning");
            }
        }
    }
}
