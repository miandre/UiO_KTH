package nu.geeks.uio_kth;

/**
 * Created by Hannes on 2016-02-21.
 */
public class Transaction {

    public String projectId;
    public float amount;
    public String person;
    public String object;

    public Transaction(String projectId, String person, String amount, String object){
        this.projectId = projectId;
        this.amount = Float.parseFloat(amount);
        this.person = person;
        this.object = object;
    }

}
