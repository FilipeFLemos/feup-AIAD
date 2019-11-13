package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Person;
import utils.Utils;
import utils.contracts.ClosestInspectorAnswerer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.awt.Point;

public class InspectorAgent extends AbstractAgent {

    private double distance = -1;
    private Queue tasksTime = new LinkedList<>();
    private Person lastPersonInQueue = null;

    public InspectorAgent() {
        state = State.IDLE;
        location = new Point(30, 30);
        setPeopleScanAgents(new Vector<>());
    }

    public double randomNumber(int i) {
        Random rand = new Random();
        return Math.round((rand.nextFloat() * i) * 100.0) / 100.0;
    }

    @Override
    protected void setup() {
        System.out.println("Hallo! Inspector-agent " + getAID().getName() + " is ready.");
        setServiceDescription("inspector");

        peopleScanAgents = Utils.findAvailableAgents(this, "scan");
        addBehaviour(Utils.lateSubscriptionFactoryMethod(this, "scan"));

        addBehaviour(new ClosestInspectorAnswerer(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        addBehaviour(new InspectLuggage());

    }

    public double getBusyTime(){
       double time = 0;
       for(Object taskTime : tasksTime){
            time += (double) taskTime + Utils.INSPECTOR_PROCESSING_TIME;
       }
       return time;
    }

    public void increaseBusyTime(Person person){

        double time = computeBusyTime(person);
        tasksTime.add(time);
        lastPersonInQueue = person;
    }

    public double computeBusyTime(Person person){
        double distance;
        if(tasksTime.isEmpty()){
            distance = Utils.distance(location, person.getLocation());
        }
        else{
            distance = Utils.distance(person.getLocation(), lastPersonInQueue.getLocation());
        }

        return Math.floor(distance/Utils.INSPECTOR_SPEED);
    }

    private class InspectLuggage extends CyclicBehaviour {
        private boolean isTick;

        public void action() {
            if (state == State.IDLE)
            {
                if (agentQueue.isEmpty()) {
                    block();
                } else {
                    state = State.MOVING;
                    isTick = false;

                    Person person = (Person) agentQueue.element();
                    distance = Utils.distance(location,person.getLocation());
                    System.out.println(myAgent.getLocalName() + ": Moving to location: " + person.getLocation());
                }
            }
            else if (state == State.MOVING)
            {
                if(!isTick)
                {
                    isTick = true;
                    myAgent.addBehaviour(new WakerBehaviour(myAgent, 10) {
                        @Override
                        protected void onWake() {
                            double newDistance = distance - Utils.INSPECTOR_SPEED * 0.01;
                            if(newDistance > 0 ){
                                distance = newDistance;
                            }
                            else{
                                inspectLuggage();
                                distance = 0;
                            }
                            isTick = false;
                        }
                    });
                }
            }
        }


        private void inspectLuggage()
        {
            state = State.WORKING;
            Person person = (Person) agentQueue.element();
            setLocation(person.getLocation());
            System.out.println(myAgent.getLocalName() + ": Going to start inspect the luggage of Person (ID: "
                    + person.getId() + ")");
            myAgent.addBehaviour(new WakerBehaviour(myAgent, Utils.getMilliSeconds(Utils.LUGGAGE_PROCESSING_TIME)) {
                @Override
                protected void onWake() {
                    Person person = (Person) agentQueue.element();
                    System.out.println(myAgent.getLocalName() + ": Finished inspecting the luggage of Person (ID: "
                            + person.getId() + ")");
                    Utils.allocatePersonToBeScanned(myAgent);
                    tasksTime.poll();
                }
            });
        }
    }

}
