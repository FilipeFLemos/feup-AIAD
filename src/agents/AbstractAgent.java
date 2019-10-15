package agents;

import jade.core.AID;
import jade.core.Agent;

public abstract class AbstractAgent extends Agent {

    private int queueSize = 0;
    private AID queueManager = null;


    @Override
    public int getQueueSize() {
        return queueSize;
    }

    public AID getQueueManager() {
        return queueManager;
    }

    @Override
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setQueueManager(AID queueManager) {
        this.queueManager = queueManager;
    }

    public void increaseQueueSize(){
        queueSize++;
    }

    public void decreaseQueueSize(){
        queueSize--;
    }
}
