import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JADELauncher {

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();

		Profile p1 = new ProfileImpl();
		//p1.setParameter(...);
		ContainerController mainContainer = rt.createMainContainer(p1);
		
		Profile p2 = new ProfileImpl();
		//p2.setParameter(...);
		ContainerController container = rt.createAgentContainer(p2);

		AgentController ac1;
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
		}
	}

}
