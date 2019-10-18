import agents.InspectorAgent;
import agents.LuggageAgent;
import agents.PeopleScanAgent;
import agents.QueueManagerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Utils;

import java.util.ArrayList;

public class Launcher {

	private ArrayList<LuggageAgent> luggageAgents;
	private ArrayList<PeopleScanAgent> peopleScanAgents;
	private ArrayList<InspectorAgent> inspectorAgents;
	private QueueManagerAgent queueManagerAgent;

	private static ContainerController mainContainer;

	public Launcher(){
		luggageAgents = new ArrayList<>();
		peopleScanAgents = new ArrayList<>();
		inspectorAgents = new ArrayList<>();

		try {
			mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma()).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		startAgents();

		queueManagerAgent.allocateLuggage();
		queueManagerAgent.allocatePerson();
	}

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		//p1.setParameter(...);
		mainContainer = rt.createMainContainer(p1);
		
		Profile p2 = new ProfileImpl();
		//p2.setParameter(...);
		ContainerController container = rt.createAgentContainer(p2);

		Launcher l = new Launcher();

		/*AgentController ac1;
		try {
			ac1 = mainContainer.acceptNewAgent("Antonio", new ListeningAgent());
			ac1.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		Object[] agentArgs = new Object[0];
		AgentController ac2;
		try {
			ac2 = container.createNewAgent("Rui", "jade.core.Agent", agentArgs);
			ac2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}


		AgentController ac5;
		try {
			ac5 = container.createNewAgent("Seller1", "bookTrading.BookSellerAgent", agentArgs);
			ac5.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac6;
		try {
			ac6 = container.createNewAgent("Seller2", "bookTrading.BookSellerAgent", agentArgs);
			ac6.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac4;
		try {
			ac4 = container.createNewAgent("Buyer1", "bookTrading.BookBuyerAgent", new String[]{"TINTIM"});
			ac4.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		AgentController ac3;
		try {
			ac3 = mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma());
			ac3.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}*/

	}

	private void startAgents() {
		for (int i = 0; i < Utils.NUM_LUGGAGE_AGENTS; i++) {
			LuggageAgent luggageAgent = new LuggageAgent();
			try {
				mainContainer.acceptNewAgent("luggageControl" + i, luggageAgent).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			luggageAgents.add(luggageAgent);
		}

		for (int i = 0; i < Utils.NUM_PEOPLE_AGENTS; i++) {
			PeopleScanAgent peopleScanAgent = new PeopleScanAgent();
			try {
				mainContainer.acceptNewAgent("peopleScanner" + i, peopleScanAgent).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			peopleScanAgents.add(peopleScanAgent);
		}

		for (int i = 0; i < Utils.NUM_INSPECTOR_AGENTS; i++) {
			InspectorAgent inspectorAgent = new InspectorAgent();
			try {
				mainContainer.acceptNewAgent("inspector" + i, inspectorAgent).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			inspectorAgents.add(inspectorAgent);
		}

		queueManagerAgent = new QueueManagerAgent();
		try {
			mainContainer.acceptNewAgent("queueManager", queueManagerAgent).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

}
