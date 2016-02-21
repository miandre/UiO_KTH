package nu.geeks.uio_kth;

/**
 *
 *
 * Created by Hannes on 2016-02-21.
 */
public class Person {

    public String name;
    public float amount;

    public Person(String name, float amount){
        this.name = name;
        this.amount = amount;
    }

    public boolean isSame(String name2){
        return name.equals(name2);
    }

}
