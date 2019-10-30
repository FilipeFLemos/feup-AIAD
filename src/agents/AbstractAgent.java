package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import models.Person;

import java.util.Vector;

public abstract class AbstractAgent extends Agent {

    Vector<AID> luggageAgents;
    Vector<AID> peopleScanAgents;
    private int queueSize = 0;

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

    public abstract void movedPerson();

    public abstract Person getPerson();

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

    @Override
    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void increaseQueueSize() {
        queueSize++;
    }

    public void decreaseQueueSize() {
        queueSize--;
    }

}
