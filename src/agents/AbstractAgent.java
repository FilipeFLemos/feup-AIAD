package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import models.Person;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class AbstractAgent extends Agent {

    Queue agentQueue = new LinkedList<Person>();
    Vector<AID> luggageAgents;
    Vector<AID> peopleScanAgents;

    void setServiceDescription(String type) {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public boolean isQueueEmpty(){
        return agentQueue.isEmpty();
    }

    public void enqueue(Person person){
        agentQueue.add(person);
    }

    public void movedPerson(){
        agentQueue.poll();
    }

    public Person getPerson(){
        if(!agentQueue.isEmpty()){
            return (Person) agentQueue.peek();
        }
        return null;
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

    void setPeopleScanAgents(Vector<AID> peopleScanAgents) {
        this.peopleScanAgents = peopleScanAgents;
    }

    public Vector<AID> getLuggageAgents() {
        return luggageAgents;
    }

    public void setLuggageAgents(Vector<AID> luggageAgents) {
        this.luggageAgents = luggageAgents;
    }

    public int getAgentQueueSize(){
        return agentQueue.size();
    }
}
