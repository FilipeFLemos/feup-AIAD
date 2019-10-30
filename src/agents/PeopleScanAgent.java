package agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.contracts.QueueSizeAnswerer;

public class PeopleScanAgent extends AbstractAgent {

    @Override
    protected void setup() {
        System.out.println("Hallo! PeopleScan-agent " + getAID().getName() + " is ready.");
        setServiceDescription("scan");

        addBehaviour(new QueueSizeAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

    @Override
    public void movedPerson() {
    }

    @Override
    public Person getPerson() {
        return null;
    }
}
