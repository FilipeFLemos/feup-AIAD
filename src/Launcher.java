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

import static utils.Utils.*;

public class Launcher {

    private static ContainerController mainContainer;
    private QueueManagerAgent queueManagerAgent;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(MAX_THREADS);
    private Queue waitingQueue;
    private boolean stopSystem;
    private int personId = 0;
    private ArrayList<PeopleScanAgent> peopleScanAgents;

    public Launcher() {

        stopSystem = false;
        resetSystem();
    }

    public static void main(String[] args) {
        new Launcher();
    }

    public void resetSystem(){
        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        mainContainer = rt.createMainContainer(p1);
        waitingQueue = new LinkedList<Person>();
        peopleScanAgents = new ArrayList<>();

        randomIndependentVars();

        try {
            mainContainer.acceptNewAgent("myRMA", new jade.tools.rma.rma()).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        startAgents();
        scheduledExecutorService.schedule(this::allocatePerson, 0, TimeUnit.MILLISECONDS);

        int randomWait = Utils.getRandom(0, Utils.QUEUE_MAX_FREQUENCY);
        scheduledExecutorService.schedule(this::enqueue, randomWait, TimeUnit.MILLISECONDS);

        long initTime = System.currentTimeMillis();
        while(true){
            long time = System.currentTimeMillis();
            if(time > initTime + 1000){
                stopSystem = true;
                break;
            }
        }
        calcAverageTime();

    }

    private void calcAverageTime()
    {
        System.out.println("Finito");
        //TODO: calcular tempo
        //TODO: Guardar no ficheiro

        scheduledExecutorService.schedule(this::resetSystem, 0, TimeUnit.MILLISECONDS);
    }

    private void randomIndependentVars(){
        NUM_LUGGAGE_AGENTS = Utils.getRandom(1, 5);
        NUM_INSPECTOR_AGENTS = Utils.getRandom(1, 5);
        NUM_PEOPLE_AGENTS = Utils.getRandom(1, 5);

        LUGGAGE_PROCESSING_TIME = Utils.getRandom(5, 10);
        INSPECTOR_PROCESSING_TIME = Utils.getRandom(5, 10);
        SCANNING_TIME = Utils.getRandom(5, 10);

        MAX_LUGGAGE_CAPACITY = Utils.getRandom(1, 5);

        PROBABILITY_IRREGULAR = Utils.getRandom(0, 100);
        QUEUE_MAX_FREQUENCY = Utils.getRandom(5, 15);
    }

    private void startAgents() {
        Object[] args = new Object[2];
        args[0] = 0;
        args[1] = 0;

        int xL = 0;
        int yL = 15;

        for (int i = 0; i < Utils.NUM_LUGGAGE_AGENTS; i++) {

            LuggageAgent luggageAgent = new LuggageAgent(xL,yL);
            try {
                mainContainer.acceptNewAgent("luggageControl" + i, luggageAgent).start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }

            xL += 5;
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
        //System.out.println("queueManager: A Person " + person.getPersonType() + " has joined the Queue!");
        waitingQueue.add(person);
        int randomWait = Utils.getRandom(0, Utils.QUEUE_MAX_FREQUENCY);
        scheduledExecutorService.schedule(this::enqueue, randomWait, TimeUnit.MILLISECONDS);
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

//            try {
//                Thread.sleep(Utils.REQUERY_DELAY);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

}
