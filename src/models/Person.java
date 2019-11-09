package models;

import java.io.Serializable;
import java.util.Random;
import java.awt.Point;

public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int waitingTimeInSeconds;
    private PersonType personType;
    private boolean hasIrregularLuggage;
    private Point location;

    public Person(PersonType personType, int id) {
        this.waitingTimeInSeconds = 0;
        this.personType = personType;
        this.location = new Point(0, 0);
        this.id = id;

        if ((personType.toString()).equals("Luggage")) {
            this.randomizeHasIrregularLuggage();
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

    public void setLocation(int x, int y) {
        Point location = new Point(x, y);
        this.location = location;
    }

    public void setLocation(Point p) {
        this.setLocation(p.x, p.y);
    }

    public Point getLocation() {
        return this.location;
    }

    public void stopTimer() {

    }

    public void randomizeHasIrregularLuggage() {
        Random rand = new Random();
        boolean isIrregular = (rand.nextInt(2) < 1) ? true : false;
        setHasIrregularLuggage(isIrregular);
    }
}
