package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import models.Person;
import java.awt.Point;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class AbstractAgent extends Agent {

    Queue agentQueue = new LinkedList<Person>();
    Vector<AID> luggageAgents;
    Vector<AID> peopleScanAgents;
    Vector<AID> inspectorAgents;
    State state;
    Point location = new Point(0,0);

    void parseArgs(){
        Object[] args = getArguments();
        if(args == null){
            return;
        }
        int x = Integer.parseInt(args[0].toString());
        int y = Integer.parseInt(args[1].toString());
        location = new Point(x,y);
    }

    void setServiceDescription(String type) {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] dfds = DFService.search(this, dfAgentDescription);
            if(dfds.length >= 1) {
                DFService.modify(this, dfAgentDescription);
            } else {
                DFService.register(this, dfAgentDescription);
            }
        } catch (FIPAException fe) {
            try {
                DFService.deregister(this);
                DFService.register(this,dfAgentDescription);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public boolean isQueueEmpty() {
        return agentQueue.isEmpty();
    }

    public int getAgentQueueSize() {
        return agentQueue.size();
    }

    public void enqueue(Person person) {
        agentQueue.add(person);
    }

    public void movedPerson() {
        agentQueue.poll();
    }

    public Person getPerson() {
        if (!agentQueue.isEmpty()) {
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

    void setLuggageAgents(Vector<AID> luggageAgents) {
        this.luggageAgents = luggageAgents;
    }

    public Vector<AID> getInspectorAgents() {
        return inspectorAgents;
    }

    public void setInspectorAgents(Vector<AID> inspectorAgents) {
        this.inspectorAgents = inspectorAgents;
    }

    public void setLocation(Point p) {
        this.location = p;
    }

    public Point getLocation() {
        return this.location;
    }

    public void setStateIdle() {
        state = State.IDLE;
    }

    enum State {
        IDLE,
        WORKING,
        MOVING
    }
}
