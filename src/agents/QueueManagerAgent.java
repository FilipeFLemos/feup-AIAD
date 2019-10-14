package agents;

import jade.core.AID;
import jade.core.Agent;

import java.util.Vector;

public class QueueManagerAgent extends Agent {

    private Vector<AID> luggageAgents;
    private Vector<AID> peopleScanAgents;

    public QueueManagerAgent(){
        luggageAgents = new Vector<>();
        peopleScanAgents = new Vector<>();
    }
}
