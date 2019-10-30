package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import models.Person;
import utils.Utils;
import utils.contracts.QueueSizeQuery;

import java.util.Vector;

public class QueueManagerAgent extends AbstractAgent {

    private Vector<AID> luggageAgents;
    private Person person;

    public QueueManagerAgent() {
        luggageAgents = new Vector<>();
        setPeopleScanAgents(new Vector<>());
        person = null;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! manager-agent " + getAID().getName() + " is ready.");
        setServiceDescription("manager");

        findAvailableAgents();
        acceptNewAgents();
    }

    private void findAvailableAgents() {
        luggageAgents = Utils.findAvailableAgents(this, "luggage");
        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
    }

    private void acceptNewAgents() {
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "luggage"));
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));
    }

    public void allocateLuggage() {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setContent("How many luggage can you receive?");
        addBehaviour(new QueueSizeQuery(this, msg, "luggage"));
    }

    public Vector<AID> getLuggageAgents() {
        return luggageAgents;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public void movedPerson() {
        person = null;
    }
}
