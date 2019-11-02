package models;

import java.io.Serializable;
import java.util.Random;

public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int waitingTimeInSeconds;
    private PersonType personType;
    private boolean hasIrregularLuggage = false;

    public Person(PersonType personType, int id) {
        this.waitingTimeInSeconds = 0;
        this.personType = personType;
        this.id = id;

        if (personType.equals("Luggage")) {
            hasIrregularLuggage = this.randomizeHasIrregularLuggage();
        }
    }

    public void increaseWaitingTimeBy(int time) {
        waitingTimeInSeconds += time;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public boolean getHasIrregularLuggage() {
        return hasIrregularLuggage;
    }

    public void setHasIrregularLuggage(boolean isIrregular) {
        hasIrregularLuggage = isIrregular;
    }

    public void stopTimer() {

    }

    public boolean randomizeHasIrregularLuggage() {
        Random rand = new Random();
        if (rand.nextInt(101) > 90) {
            return true;
        }
        return false;
    }
}
