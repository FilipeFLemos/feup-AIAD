package models;

import java.io.Serializable;

public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int waitingTimeInSeconds;
    private PersonType personType;

    public Person(PersonType personType, int id) {
        this.waitingTimeInSeconds = 0;
        this.personType = personType;
        this.id = id;
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
}
