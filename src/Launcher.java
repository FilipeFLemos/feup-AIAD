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
        scheduledExecutorService.schedule(this::allocatePerson, 0, TimeUnit.MILLISECONDS);

        int randomWait = Utils.getRandom(0, Utils.QUEUE_MAX_FREQUENCY);
        scheduledExecutorService.schedule(this::enqueue, randomWait, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        mainContainer = rt.createMainContainer(p1);

        new Launcher();
    }

    private void startAgents() {
        Object[] args = new Object[2];
        args[0] = 0;
        args[1] = 0;

        for (int i = 0; i < Utils.NUM_LUGGAGE_AGENTS; i++) {

            try {
                mainContainer.createNewAgent("luggageControl" + i, "agents.LuggageAgent", args).start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < Utils.NUM_PEOPLE_AGENTS; i++) {

            try {
                mainContainer.createNewAgent("peopleScanner" + i, "agents.PeopleScanAgent", args).start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < Utils.NUM_INSPECTOR_AGENTS; i++) {

            try {
                mainContainer.createNewAgent("inspector" + i, "agents.InspectorAgent", args).start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
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
        System.out.println("Person Type: " + person.getPersonType());
        waitingQueue.add(person);
        int randomWait = Utils.getRandom(0, Utils.QUEUE_MAX_FREQUENCY);
        scheduledExecutorService.schedule(this::enqueue, randomWait, TimeUnit.SECONDS);
    }

    private Person generatePerson() {
        Person person;
        int randomPersonType = Utils.getRandom(0, 4);
        switch (randomPersonType) {
        case 0:
            person = new Person(PersonType.Empty, personId);
            break;
        default:
            person = new Person(PersonType.Luggage, personId);
            break;
        }
        personId++;
        return person;
    }

    private void allocatePerson() {
        while (!stopSystem) {

            if(!queueManagerAgent.isSystemReady()){
                continue;
            }

            if (!waitingQueue.isEmpty() && queueManagerAgent.isQueueEmpty()) {
                Person person = (Person) waitingQueue.poll();
                if (person == null) {
                    return;
                }
                queueManagerAgent.enqueue(person);
                switch (person.getPersonType()) {
                    case Empty:
                        System.out.println(queueManagerAgent.getLocalName() + ": The person without luggage (ID: "
                                + person.getId() + ") is being allocated..." + queueManagerAgent.getPerson().getId());
                        Utils.allocatePersonToBeScanned(queueManagerAgent);
                        break;
                    case Luggage:
                        System.out.println(queueManagerAgent.getLocalName() + ": The person with luggage (ID: "
                                + person.getId() + ") is being allocated..." + queueManagerAgent.getPerson().getId());
                        queueManagerAgent.allocateLuggage();
                        break;
                }
            }

            try {
                Thread.sleep(Utils.getMilliSeconds(Utils.REQUERY_DELAY));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
