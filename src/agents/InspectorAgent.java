package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import utils.Utils;

import java.util.Vector;

public class InspectorAgent extends Agent {

    private Vector<AID> peopleScanAgents;

    public InspectorAgent(){
        peopleScanAgents = new Vector<>();
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Inspector-agent "+getAID().getName()+" is ready.");
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("inspector");
        serviceDescription.setName(getLocalName());
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        peopleScanAgents = Utils.findAvailableScanAgents(this);
        Utils.acceptNewScanAgents(this);
    }

    public Vector<AID> getPeopleScanAgents() {
        return peopleScanAgents;
    }

}
