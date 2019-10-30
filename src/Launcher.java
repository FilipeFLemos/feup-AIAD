import agents.InspectorAgent;
import agents.LuggageAgent;
import agents.PeopleScanAgent;
import agents.QueueManagerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import models.Person;
import models.PersonType;
import utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.Utils.MAX_THREADS;

public class Launcher {

    private static ContainerController mainContainer;
    private ArrayList<LuggageAgent> luggageAgents;
    private ArrayList<PeopleScanAgent> peopleScanAgents;
    private ArrayList<InspectorAgent> inspectorAgents;
    private QueueManagerAgent queueManagerAgent;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(MAX_THREADS);
    private Queue waitingQueue;
    private boolean stopSystem;
    private int personId = 0;

    public Launcher() {
        luggageAgents = new ArrayList<>();
        peopleScanAgents = new ArrayList<>();
        inspectorAgents = new ArrayList<>();
        waitingQueue = new LinkedList<Person>();
        stopSystem = false;

        try {
            mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma()).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        startAgents();

        while (!stopSystem) {
            int randomWait = Utils.getRandom(0, Utils.QUEUE_MAX_FREQUENCY);
            scheduledExecutorService.schedule(this::enqueue, randomWait, TimeUnit.SECONDS);
            scheduledExecutorService.schedule(this::allocatePerson, 1, TimeUnit.MILLISECONDS);
        }
    }

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        // p1.setParameter(...);
        mainContainer = rt.createMainContainer(p1);

        Profile p2 = new ProfileImpl();
        // p2.setParameter(...);
        ContainerController container = rt.createAgentContainer(p2);

        Launcher l = new Launcher();
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

        luggageAgents.get(0).setHasIrregularLuggage(true);
        luggageAgents.get(1).setHasIrregularLuggage(true);

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

    private void enqueue() {
        Person person = generatePerson();
        waitingQueue.add(person);
    }

    private Person generatePerson() {
        Person person = null;
        int randomPersonType = Utils.getRandom(0, 1);
        switch (randomPersonType) {
            case 0:
                person = new Person(PersonType.Empty, personId);
                break;
            case 1:
                person = new Person(PersonType.Luggage, personId);
                break;
        }
        personId++;
        return person;
    }

    private void allocatePerson() {
        if (!waitingQueue.isEmpty() && queueManagerAgent.isQueueEmpty()) {
            Person person = (Person) waitingQueue.poll();
            if (person == null) {
                return;
            }
            queueManagerAgent.enqueue(person);
            switch (person.getPersonType()) {
                case Empty:
                    System.out.println(queueManagerAgent.getLocalName() + ": The person without luggage (ID: " + person.getId() + ") is being allocated..."+ queueManagerAgent.getPerson().getId());
                    Utils.allocatePersonToBeScanned(queueManagerAgent);
                    break;
                case Luggage:
                    System.out.println(queueManagerAgent.getLocalName() + ": The person with luggage (ID: " + person.getId() + ") is being allocated..." + queueManagerAgent.getPerson().getId());
                    queueManagerAgent.allocateLuggage();
                    break;
            }
        }
    }

}
