package nu.geeks.uio_kth.Objects;

/**
 *
 * @author anton_000
 */
public class Payment {
    public String from;
    public String to;
    public float amount;

    Payment(String from, String to, float amount){
        this.from=from;
        this.to=to;
        this.amount=amount;
    }
}