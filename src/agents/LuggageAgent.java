package agents;

import jade.core.AID;
import jade.core.Agent;
import utils.Utils;

public class LuggageAgent extends Agent {

    private int trailFreeSpace;
    private AID queueManager = null;

    public LuggageAgent(){
        trailFreeSpace = Utils.MAX_LUGGAGE_CAPACITY;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Luggage-agent "+getAID().getName()+" is ready.");
    }
}
