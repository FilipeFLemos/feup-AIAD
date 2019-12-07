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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    private ArrayList<InspectorAgent> inspectorAgents;
    private ArrayList<LuggageAgent> luggageAgents;
    private Runtime rt;
    private Profile p1;

    public Launcher() {
        resetSystem();
    }

    public static void main(String[] args) {
        new Launcher();
    }

    public void resetSystem(){
        waitingQueue = new LinkedList<Person>();
        randomIndependentVars();
        stopSystem = false;

        rt = Runtime.instance();
        p1 = new ProfileImpl();
        mainContainer = rt.createMainContainer(p1);
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
            if(time > initTime + 5000){
                stopSystem = true;
                break;
            }
        }
        calcAverageTime();

    }

    private void calcAverageTime()
    {
        System.out.println("Finito");
        int numPersons = 0;
        int totalTime = 0;

        for(PeopleScanAgent peopleScanAgent : peopleScanAgents){
            numPersons += peopleScanAgent.numPeople;
            totalTime += peopleScanAgent.totalTime;
        }

        int averageTime = totalTime/numPersons;

        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            String line = Utils.NUM_LUGGAGE_AGENTS + "," + Utils.NUM_PEOPLE_AGENTS + "," + Utils.NUM_INSPECTOR_AGENTS + "," + Utils.LUGGAGE_PROCESSING_TIME + "," + Utils.INSPECTOR_PROCESSING_TIME + "," + Utils.INSPECTOR_PROCESSING_TIME + "," + Utils.SCANNING_TIME + "," + Utils.MAX_LUGGAGE_CAPACITY + "," + Utils.PROBABILITY_IRREGULAR + "," + Utils.QUEUE_MAX_FREQUENCY + "," + averageTime;
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        killAgents();
        scheduledExecutorService.schedule(this::resetSystem, 100, TimeUnit.MILLISECONDS);
    }

    private void killAgents(){
        for (PeopleScanAgent agent : peopleScanAgents) {
            agent.takeDown();
        }

        for (LuggageAgent agent : luggageAgents) {
            agent.takeDown();
        }

        for (InspectorAgent agent : inspectorAgents) {
            agent.takeDown();
        }

        queueManagerAgent.takeDown();
    }

    private void randomIndependentVars(){
        NUM_LUGGAGE_AGENTS = Utils.getRandom(1, 5);
        NUM_INSPECTOR_AGENTS = Utils.getRandom(1, 5);
        NUM_PEOPLE_AGENTS = Utils.getRandom(1, 5);

        LUGGAGE_PROCESSING_TIME = Utils.getRandom(5, 15);
        INSPECTOR_PROCESSING_TIME = Utils.getRandom(5, 15);
        SCANNING_TIME = Utils.getRandom(5, 10);

        MAX_LUGGAGE_CAPACITY = Utils.getRandom(1, 5);

        PROBABILITY_IRREGULAR = Utils.getRandom(0, 100);
        QUEUE_MAX_FREQUENCY = Utils.getRandom(1, 10);
    }

    private void startAgents() {
        peopleScanAgents = new ArrayList<>();
        luggageAgents = new ArrayList<>();
        inspectorAgents = new ArrayList<>();

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

    private void enqueue() {
        Person person = generatePerson();
        //System.out.println("queueManager: A Person " + person.getPersonType() + " has joined the Queue!");
        waitingQueue.add(person);
        scheduledExecutorService.schedule(this::enqueue, Utils.QUEUE_MAX_FREQUENCY, TimeUnit.MILLISECONDS);
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
