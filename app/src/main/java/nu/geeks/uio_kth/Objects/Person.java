package nu.geeks.uio_kth.Objects;

import java.lang.Math.*;
import static java.lang.Math.abs;

/**
 *
 *
 * Created by Hannes on 2016-02-21.
 */
public class Person implements Comparable {

    public String name;
    public float amount;
    public boolean done;

    public Person(String name, float amount){
        this.name = name;
        this.amount = amount;
        done = false;
    }
    @Override
    public int compareTo(Object per) {
        Person p = (Person) per;
        if (abs(this.amount) == abs(p.amount)) {
            return 0;
        } else {
            return abs(this.amount) > abs(p.amount) ? 1 : -1;
        }
    }

    public boolean isSame(String name2){
        return name.equals(name2);
    }

}
