package nu.geeks.uio_kth;

/**
 * Created by Hannes on 2016-02-21.
 */
public class Transaction {

    public String projectId;
    public int amount;
    public String person;
    public String object;

    public Transaction(String projectId, String amount, String person, String object){
        this.projectId = projectId;
        this.amount = Integer.parseInt(amount);
        this.person = person;
        this.object = object;
    }

}
